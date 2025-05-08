package io.github.johnlucasweb.quarkussocial.rest;

import io.github.johnlucasweb.quarkussocial.domain.model.User;
import io.github.johnlucasweb.quarkussocial.domain.repository.PostRepository;
import io.github.johnlucasweb.quarkussocial.domain.repository.UserRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;

    public PostResource(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @POST
    public Response savePost(@PathParam("userId") Long userId){

        User user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId){
        return Response.ok().build();
    }

}
