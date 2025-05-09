package io.github.johnlucasweb.quarkussocial.rest;

import io.github.johnlucasweb.quarkussocial.domain.model.Follower;
import io.github.johnlucasweb.quarkussocial.domain.model.User;
import io.github.johnlucasweb.quarkussocial.domain.repository.FollowerRepository;
import io.github.johnlucasweb.quarkussocial.domain.repository.UserRepository;
import io.github.johnlucasweb.quarkussocial.rest.dto.FollowerRequest;
import io.github.johnlucasweb.quarkussocial.rest.dto.FollowerResponse;
import io.github.johnlucasweb.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest followerRequest) {

        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (user.getId().equals(followerRequest.getFollowerId())) {
            var mensagem = "You can't follow yourself";
            return Response.status(Response.Status.CONFLICT).entity(mensagem).build();
        }

        var follower = userRepository.findById(followerRequest.getFollowerId());

        boolean follows = followerRepository.follows(user, follower);
        if (!follows) {
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            followerRepository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();

    }

    @GET
    public Response listFollowers(
            @PathParam("userId") Long userId) {

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }



        var list = followerRepository.findFollowersByUser(userId);

        FollowersPerUserResponse followersPerUserResponse = new FollowersPerUserResponse();
        followersPerUserResponse.setFollowersCount(list.size());

        var follwerList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        followersPerUserResponse.setContent(follwerList);

        return Response.ok(followersPerUserResponse).build();

    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId") Long followerId) {

        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerIdAndUserId(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();

    }


}
