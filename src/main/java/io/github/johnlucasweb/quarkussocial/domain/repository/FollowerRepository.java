package io.github.johnlucasweb.quarkussocial.domain.repository;

import io.github.johnlucasweb.quarkussocial.domain.model.Follower;
import io.github.johnlucasweb.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User user, User follower) {

        Map<String, Object> params = new HashMap<>();
        params.put("follower", follower);
        params.put("user", user);

        PanacheQuery<Follower> query = find("follower =:follower and user = :user", params);
        Optional<Follower> followerOptional = query.firstResultOptional(); // Optional<Follower>

        return followerOptional.isPresent();

    }

    public List<Follower> findFollowersByUser(Long userId) {
        return find("user.id", userId).list(); // List<Follower>
    }

    public void deleteByFollowerIdAndUserId(Long followerId, Long userId) {

        var params = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();

        delete("follower.id = :followerId and user.id = :userId", params);
    }
}
