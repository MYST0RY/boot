import openai
from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
from pyngrok import ngrok
from transformers import pipeline
import os
import json
import requests

# OpenAI API 키 설정
openai.api_key = "OPENAI_API_KEY"

# FastAPI 앱 생성
app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 모든 도메인 허용
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 감정 데이터 모델 정의
class EmotionData(BaseModel):
    feel: str

# 책 데이터를 저장할 변수
book_recommendations = []

# 감정 분석 파이프라인
emotion_analyzer = pipeline('text-classification', model='bhadresh-savani/bert-base-uncased-emotion')

# 한→영 번역 함수
def translate_to_english(feel: str) -> str:
    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": f"Translate this Korean text into English: '{feel}'"}
        ]
    )
    translated_text = response['choices'][0]['message']['content']
    return translated_text

#======================================================================================
#!!crawling(?)
# 책 데이터를 크롤링하는 함수
def fetch_books(crawl_subjects):
    results = []
    for subject in crawl_subjects:
        url = f"https://openlibrary.org/search.json?subject={subject}&limit=5"
        response = requests.get(url)
        if response.status_code == 200:
            data = response.json()
            books = data.get("docs", [])
            for book in books:
                title = book.get("title", "Unknown Title")
                authors = book.get("author_name", ["Unknown Author"])
                genre = subject
                results.append({
                    "title": title,
                    "author": ", ".join(authors),
                    "genre": genre
                })
    return results
#!!crawling(?) part "끝"
#====================================================================
# GPT를 사용해 책 추천 요청
def get_book_recommendations_from_gpt(user_emotion: str, crawled_titles: list[str]):
    prompt = f"""
    A user feels '{user_emotion}'. Below are example book titles:
    {crawled_titles}

    Please recommend 3 books that resonate with this emotion. Include the title, author, and genre.
    genre is related with these. "sadness": ["literature", "historical Fiction", "poetry"],
            "love": ["romance", "Young Adult", "Magic"],
            "joy": ["Humor", "Short Stories", "plays"],
            "fear": ["thriller", "Horror", "mystery"],
            "anger": ["Mystery and detective stories", "plays", "historical Fiction"],
            "surprise": ["science Fiction", "fantasy", "Magic"]
    Respond in the following JSON format:
    {{
        "recommendations": [
            {{"title": "Book Title", "author": "Author Name", "genre": "Genre"}},
            ...
        ]
    }}
    """
    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": prompt}
        ]
    )
    gpt_answer = response['choices'][0]['message']['content']
    try:
        recommendations_data = json.loads(gpt_answer)
        return recommendations_data.get("recommendations", [])
    except json.JSONDecodeError:
        return []

# 감정 데이터 수신 → 분석 → 추천
@app.post("/send_server_emotion/")
@app.post("/send_server_emotion")
async def analyze_emotion(emotion_sended: EmotionData):
    try:
        # Step 1: 한글 → 영어 번역
        translated_emotion = translate_to_english(emotion_sended.feel)

        # Step 2: 감정 분석
        analysis_result = emotion_analyzer(translated_emotion)
        user_emotion = analysis_result[0]["label"]

        # Step 3: 감정별 카테고리 설정
        emotion_to_subjects = {
            "sadness": ["literature", "historical Fiction", "poetry"],
            "love": ["romance", "Young Adult", "Magic"],
            "joy": ["Humor", "Short Stories", "plays"],
            "fear": ["thriller", "Horror", "mystery"],
            "anger": ["Mystery and detective stories", "plays", "historical Fiction"],
            "surprise": ["science Fiction", "fantasy", "Magic"]
        }
        crawl_subjects = emotion_to_subjects.get(user_emotion, ["literature"])

        # Step 4: 데이터 크롤링
        crawled_books = fetch_books(crawl_subjects)

        # Step 5: GPT에게 책 추천 요청
        gpt_recommendations = get_book_recommendations_from_gpt(
            user_emotion=user_emotion,
            crawled_titles=[book["title"] for book in crawled_books]
        )

        # 결과 저장
        global book_recommendations
        book_recommendations = [
            {"title": rec["title"], "author": rec["author"], "genre": rec["genre"]}
            for rec in gpt_recommendations
        ]

        # 결과 반환
        return {
            "original_feel": emotion_sended.feel,
            "translated_feel": translated_emotion,
            "emotion_analysis": user_emotion,
            "recommendations": book_recommendations
        }
    except Exception as e:
        return {"error": str(e)}

# 책 추천 데이터 GET 요청
@app.get("/get_recommendations/")
async def get_recommendations():
    return book_recommendations

# ngrok 실행
if __name__ == "__main__":
    public_url = ngrok.connect(8000)
    print(f"ngrok URL: {public_url}")
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
