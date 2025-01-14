2025 인공지능개발자 부트캠프 2조 미니프로젝트 결과물입니다.

> # **프로젝트명**

**감정 기반의 도서 추천 앱**

> ### **1. 프로젝트 개요**

- **목적**: 사용자 감정을 기반으로 도서 추천 시스템을 개발하여 개인화된 콘텐츠 경험 제공.
- **핵심 기능**:
    - 사용자의 자유로운 텍스트 입력을 분석하여 맞춤형 도서 추천.
    - 인터넷 크롤링을 통한 대규모 줄거리와 리뷰 데이터 수집.
    - 줄거리와 리뷰 데이터를 통합 분석하여 감정의 신뢰도를 향상.
    - 사용자 감정과 매칭된 도서 추천.
 
> ### **2. 주요 기능 및 구현 방안**

### **1) 데이터 수집**

- **크롤링 대상 사이트**:
    - **도서 줄거리**: 알라딘
    - **사용자 리뷰**: 도서 관련 주요 리뷰 사이트.
- **수집 데이터**:
    - 줄거리, 사용자 리뷰, 평점, 장르.
- **구현 기술**:
    - Python 라이브러리 `BeautifulSoup` 및 `requests` 활용.
- **예상 결과**:
    - 최소 1000개의 도서 데이터를 확보.(적합한 데이터셋 수 찾아야 할 듯)

### **2) 데이터 처리**

- **데이터 정제**:
    - 결측치 제거, 중복 데이터 삭제.
    - 줄거리 및 리뷰 텍스트 전처리(특수문자 제거, 소문자 변환 등).
- **감정 분석 준비**:
    - Hugging Face 커뮤니티의 AI모델을 활용해 감정 분석 가능한 데이터로 변환.

### **3) 감정 분석**

- **사용 모델**:
    - 줄거리와 리뷰를 통합 감정 분석: `bhadresh-savani/bert-base-uncased-emotion`.
- **분석 로직**:
    1. **줄거리와 리뷰 데이터를 통합**:
        - 하나의 텍스트로 합쳐서 감정 분석 모델에 입력.
        - 줄거리와 리뷰의 비중은 50:50 또는 조정 가능(예: 리뷰가 많은 경우 리뷰 비중을 높임).
        
        ```
        combined_text = summary_text + " " + " ".join(review_texts)
        emotion_result = emotion_model(combined_text)
        ```
        
    2. **통합 감정 태그 생성**:
        - 감정 분석 결과에서 주요 감정 태그를 추출.
        - 예: 희망적(joy), 슬픔(sadness), 따뜻함(positive).
    3. **결과 저장**:
        - 각 도서에 대해 통합 감정 태그를 데이터셋에 추가.

### **4) 추천 시스템**

- **사용자 입력 처리**:
    - 입력: 자유 텍스트(예: "마음을 따뜻하게 해주는 책 추천해줘.").
    - 분석: 감정 및 키워드 기반 추천 로직 구현.
- **추천 로직**:
    1. 사용자 입력 텍스트를 감정 분석 모델로 처리.
    2. 입력된 감정 태그와 데이터셋 내 도서 감정 태그의 유사도를 계산.
    3. 유사도가 높은 순으로 도서를 추천.
    
    ```
    from sklearn.metrics.pairwise import cosine_similarity
    
    # 사용자 입력 감정 벡터
    user_emotion_vector = emotion_model(user_input_text)
    
    # 데이터셋 감정 벡터와 비교
    similarities = cosine_similarity(user_emotion_vector, dataset_emotion_vectors)
    
    # 유사도 높은 순으로 추천
    recommendations = dataset[np.argsort(similarities)[::-1]]
    ```
    

### **5) UI 구현 및 배포**

- **사용자 인터페이스**:
    - 피그마를 활용해 사용자 친화적 UI 설계.
    - 입력: 자유 텍스트 입력.
    - 출력: 추천 도서 목록과 감정 분석 결과.
- **앱 개발**:
    - 안드로이드 스튜디오를 기반으로 모바일 앱 구현.
    - 파이어베이스를 통한 서버 구축
- **배포 플랫폼**:
    - Streamlit 또는 Hugging Face Spaces에서 초기 프로토타입 배포.

 > ### **3. 개발 일정**

| 단계 | 세부 작업 | 기간 |
| --- | --- | --- |
| 데이터 수집 | 대상 사이트 크롤링 및 데이터 확보 |  |
| 데이터 전처리 | 정제 및 분석 가능한 형태로 데이터 가공 |  |
| 감정 분석 모델 통합 | 줄거리 및 리뷰 데이터를 통합 감정 분석 모델에 적용 |  |
| 추천 로직 구현 | 사용자 입력과 데이터 감정을 결합한 추천 설계 |  |
| UI 설계 및 앱 개발 | 피그마 설계 및 앱 프로토타입 구현 |  |
| 배포 및 테스트 | 프로토타입 배포 및 사용자 피드백 반영 |  |

---

### **4. 예상 결과물**

1. **모바일 애플리케이션**:
    - 사용자가 자유롭게 입력한 텍스트를 기반으로 도서 추천.
2. **결과 예시**:
    - 사용자 입력: "힐링 되는 책을 추천해줘."
    - 추천 출력:
        - 제목: "아무것도 아닌 지금은 없다."
        - 감정 분석 결과: 희망적(joy, 85%).
        - 리뷰 데이터와 줄거리 분석 통합으로 신뢰도 향상.
3. **배포 링크**:
    - Streamlit을 통한 초기 프로토타입.

---

### **5. 필요 리소스**

1. **기술 스택**:
    - Python 라이브러리: `BeautifulSoup`, `transformers`, `pandas`, `streamlit`.
    - Figma: UI 설계 도구.
    - Hugging Face 모델 API.
2. **데이터 소스**:
    - 알라딘, YES24, 사용자 리뷰 사이트.
3. **팀 역할 분담**:
    - 데이터 크롤링: 팀원 A, B.
    - 모델 통합 및 감정 분석: 팀원 C.
    - 추천 로직 구현 및 UI 설계: 팀원 D.

---

### **6. 기대 효과 및 차별성**

1. **개인화된 추천**:
    - 사용자의 자유로운 텍스트 입력을 기반으로 맞춤형 추천 제공.
2. **대규모 데이터 결합**:
    - 줄거리와 리뷰 데이터를 통합 분석하여 추천 정확도 증대.
3. **신뢰도 향상**:
    - 리뷰 데이터를 활용하여 감정 분석의 오류를 보완하며 추천 품질 강화.
    
4. **사용자 친화적 접근**:
    - 감정과 선호도를 기반으로 쉽고 직관적인 추천 제공.
