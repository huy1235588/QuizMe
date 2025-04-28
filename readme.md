## **4. Các Điểm Cuối Danh Mục**

### **4.1 Lấy Tất Cả Danh Mục**

Lấy tất cả các danh mục quiz.

- **URL**: `/api/categories`
- **Phương Thức**: `GET`
- **Yêu Cầu Xác Thực**: Không
- **Phản Hồi Thành Công**:
    - **Mã**: `200 OK`
    - **Nội Dung**:
        
        ```json
        {
            "status": "success",
            "data": [
                {
                    "id": 1,
                    "name": "Geography",
                    "description": "Explore the world through geography questions, from countries, cities to natural wonders!",
                    "iconUrl": "https://res.cloudinary.com/your_cloud_name/image/upload/image/upload/category_icons/category_1_1745400000.svg",
                    "quizCount": 2,
                    "totalPlayCount": 0,
                    "isActive": true,
                    "createdAt": "2025-04-25T23:25:03",
                    "updatedAt": "2025-04-28T19:11:34"
                }
            ],
            "message": "Categories retrieved successfully"
        }
        ```
        

### **4.2 Lấy Danh Mục Theo ID**

Lấy một danh mục cụ thể theo ID.

- **URL**: `/api/categories/{id}`
- **Phương Thức**: `GET`
- **Yêu Cầu Xác Thực**: Không
- **Tham Số URL**:
    - `id`: ID danh mục
- **Phản Hồi Thành Công**:
    - **Mã**: `200 OK`
    - **Nội Dung**:
        
        ```json
        {
          "status": "success",
          "data": {
            "id": 1,
            "name": "Mathematics",
            "description": "Test your math skills",
            "icon_url": "https://res.cloudinary.com/your_cloud_name/image/upload/category_123_1618584900.png",
            "created_at": "2025-01-01T00:00:00Z",
            "updated_at": "2025-01-01T00:00:00Z"
          }
        }
        
        ```
        
- **Phản Hồi Lỗi**:
    - **Mã**: `404 Not Found`
    - **Nội Dung**:
        
        ```json
        {
            "status": "error",
            "data": null,
            "message": "Category not found with id: 13"
        }
        
        ```
        

### **4.3 Lấy tất cả danh mục hoạt động**

Lấy tất cả các danh mục hoạt động.

- **URL**: `/api/categories/active`
- **Phương Thức**: `GET`
- **Yêu Cầu Xác Thực**: Không
- **Phản Hồi Thành Công**:
    - **Mã**: `200 OK`
    - **Nội Dung**:
        
        ```json
        {
            "status": "success",
            "data": [
                {
                    "id": 1,
                    "name": "Geography",
                    "description": "Explore the world through geography questions, from countries, cities to natural wonders!",
                    "iconUrl": "https://res.cloudinary.com/your_cloud_name/image/upload/image/upload/category_icons/category_1_1745400000.svg",
                    "quizCount": 2,
                    "totalPlayCount": 0,
                    "isActive": true,
                    "createdAt": "2025-04-25T23:25:03",
                    "updatedAt": "2025-04-28T19:11:34"
                }
            ],
            "message": "Active categories retrieved successfully"
        }
        ```
        

## **5. Các Điểm Cuối Quản Lý Quiz**

### **5.1 Lấy Tất Cả Quiz**

Lấy tất cả các quiz với tùy chọn lọc.

- **URL**: `/api/quizzes`
- **Phương Thức**: `GET`
- **Yêu Cầu Xác Thực**: Không
- **Tham Số Truy Vấn**:
    - `page` (tùy chọn): Số trang cho phân trang (mặc định: 0, bắt đầu từ 0)
    - `pageSize` (tùy chọn): Số lượng mục trên mỗi trang (mặc định: 10)
    - `category` (tùy chọn): Lọc theo ID danh mục
    - `difficulty` (tùy chọn): Lọc theo độ khó (EASY, MEDIUM, HARD)
    - `search` (tùy chọn): Tìm kiếm trong tiêu đề và mô tả
    - `sort` (tùy chọn): Sắp xếp kết quả (newest, popular)
    - `isPublic` (tùy chọn): Lọc theo trạng thái công khai (true, false)
    - `tab` (tùy chọn): Lọc theo tab (newest, popular)
- **Phản Hồi Thành Công**:
    - **Mã**: `200 OK`
    - **Nội Dung**:
        
        ```json
        {
          "code": 200,
          "status": "success",
          "message": "Paged quizzes retrieved successfully",
          "data": {
            "content": [
              {
                "id": 1,
                "title": "Flags of World Quiz",
                "description": "Guess the country based on the displayed national flag.",
                "quizThumbnails": "https://res.cloudinary.com/example/image/upload/quiz_thumbnail_1_1745400000.jpg",
                "categoryId": 1,
                "categoryName": "Geography",
                "creatorId": 3,
                "creatorName": "John Doe",
                "difficulty": "MEDIUM",
                "isPublic": true,
                "playCount": 0,
                "questionCount": 20,
                "createdAt": "2025-04-25T10:30:00",
                "updatedAt": "2025-04-25T10:30:00"
              }
            ],
            "pageNumber": 0,
            "pageSize": 10,
            "totalElements": 50,
            "totalPages": 5,
            "last": false
          }
        }
        ```
        
        

### **5.2 Lấy Quiz Theo ID**

Lấy một quiz cụ thể theo ID.

- **URL**: `/api/quizzes/{id}`
- **Phương Thức**: `GET`
- **Yêu Cầu Xác Thực**: Không
- **Tham Số URL**:
    - `id`: ID quiz
- **Phản Hồi Thành Công**:
    - **Mã**: `200 OK`
    - **Nội Dung**:
        
        ```json
        {
          "status": "success",
          "data": {
            "id": 1,
            "title": "Flags of World Quiz",
            "description": "Guess the country based on the displayed national flag.",
            "quizThumbnails": "https://res.cloudinary.com/example/image/upload/quiz_thumbnails/quiz_thumbnail_1_1745400000.webp",
            "categoryId": 1,
            "categoryName": "Geography",
            "creatorId": 3,
            "creatorName": "Lê Thiên Huy",
            "difficulty": "MEDIUM",
            "isPublic": true,
            "playCount": 0,
            "questionCount": 21,
            "createdAt": "2025-04-25T23:25:03",
            "updatedAt": "2025-04-27T21:56:56"
          }
        }
        
        ```