export interface Comment {
  id: number;
  content: string;
  author: string;
  createdAt: string;
  postId: number;
}

export interface CreateCommentRequest {
  content: string;
}
