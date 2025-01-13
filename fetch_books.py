import requests
import json

def fetch_books(subjects, filename):
    results = {}

    for subject in subjects:
        url = f"https://openlibrary.org/search.json?subject={subject}&limit=20"
        response = requests.get(url)

        if response.status_code == 200:
            data = response.json()
            books = data.get("docs", [])
            
            results[subject] = []
            for book in books:
                title = book.get("title", "제목 없음")
                authors = book.get("author_name", ["저자 없음"])
                isbn_list = book.get("isbn", [])
                cover_image = None

                if isbn_list:
                    cover_image = f"https://covers.openlibrary.org/b/isbn/{isbn_list[0]}-L.jpg"
                
                results[subject].append({
                    "title": title,
                    "authors": authors,
                    "cover_image": cover_image
                })
                print(f"주제: {subject}, 제목: {title}, 저자: {', '.join(authors)}, 이미지: {cover_image}")
        else:
            print(f"{subject}에 대한 데이터를 가져오는 데 실패했습니다.")

    try:
        with open(filename, "w", encoding="utf-8") as json_file:
            json.dump(results, json_file, ensure_ascii=False, indent=4)
        print(f"책 정보가 {filename} 파일에 저장되었습니다.")
    except Exception as e:
        print(f"파일 저장 중 오류 발생: {e}")


subjects = [
    "Mystery and detective stories",
    "literature",
    "historical Fiction",
    "mystery",
    "thriller",
    "Horror",
    "Humor"
    "science Fiction",
    "fantasy",
    "romance",
    "Magic",
    "Young Adult",
    "Short Stories",
    "plays",
    "poetry"
]

fetch_books(subjects, "books.json")
