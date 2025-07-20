package prm392.project.inter;

import java.util.List;

import prm392.project.model.Comment;
import prm392.project.model.DTOs.CommentRequest;
import prm392.project.model.DTOs.CommentResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentService {
    @POST("Comment")
    Call<Void> postComment(@Body CommentRequest commentRequest);

    @GET("Comment/blog/{blogId}")
    Call<List<CommentResponse>> getComments(@Path("blogId") int blogId);

}
