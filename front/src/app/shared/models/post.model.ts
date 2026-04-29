export interface Post {
  id: number;
  title: string;
  content: string;
  author: string;
  topicId: number;
  topicName: string;
  createdAt: string;
}

export interface CreatePostRequest {
  title: string;
  content: string;
  topicId: number;
}
