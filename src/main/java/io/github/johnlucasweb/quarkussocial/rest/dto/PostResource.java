package io.github.johnlucasweb.quarkussocial.rest.dto;

import io.github.johnlucasweb.quarkussocial.domain.model.Post;
import io.github.johnlucasweb.quarkussocial.domain.model.User;
import io.github.johnlucasweb.quarkussocial.domain.repository.FollowerRepository;
import io.github.johnlucasweb.quarkussocial.domain.repository.PostRepository;
import io.github.johnlucasweb.quarkussocial.domain.repository.UserRepository;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Path("users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository,
                        PostRepository postRepository,
                        FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    // Cria um novo post
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Cria o post com base nos dados da requisição
        try{
            Post post = new Post();
            post.setText(request.getText());
            post.setUser(user);
            post.setDateTime(LocalDateTime.now());

            postRepository.persist(post);

            return Response.status(Response.Status.CREATED).build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    @GET
    // Busca todos os posts
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId) {

        // Verifica se o usuário existe
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Verifica se o seguidor foi informado
        if(followerId == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Follower id is required")
                    .build();
        }

        User follower = userRepository.findById(followerId);

        // Verifica se o usuário é seguindo o autor
        boolean follows = followerRepository.follows(follower, user);

        // Se o usuário não seguir o autor, retorna um erro de permissão
        if(!follows){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Busca todos os posts do usuário, ordenados por data de criação decrescente
        var query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        var list = query.list();

        // Transforma a lista de posts em uma lista de PostResponse
        var postResponselist = list.stream()
                .map(PostResponse::fromEntity)
                .toList();

        return Response.ok(postResponselist).build();
    }

}
