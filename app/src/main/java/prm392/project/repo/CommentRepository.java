package prm392.project.repo;

import android.content.Context;
import java.util.List;

import prm392.project.factory.APIClient;
import prm392.project.inter.BlogService;
import prm392.project.inter.CommentService;
import prm392.project.model.Blog;
import prm392.project.model.DTOs.BlogRequest;
import prm392.project.model.Comment;
import prm392.project.model.DTOs.CommentRequest;
import prm392.project.model.DTOs.CommentResponse;
import prm392.project.model.Food;
import retrofit2.Call;
import retrofit2.Retrofit;
public class CommentRepository {
    private CommentService commentService;

    public CommentRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        commentService = retrofit.create(CommentService.class);
    }

    public static CommentService getCommentService(Context context) {
        return APIClient.getClient(context).create(CommentService.class);
    }

    public Call<Void> postComment(CommentRequest request) {
        return commentService.postComment(request);
    }

    public Call<List<CommentResponse>> getComments(int blogId) {
        return commentService.getComments(blogId);
    }
}
