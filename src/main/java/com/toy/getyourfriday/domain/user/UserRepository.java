package com.toy.getyourfriday.domain.user;

import com.toy.getyourfriday.domain.scraping.ModelUrl;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findByMonitoredUrl(ModelUrl monitoredUrl);
    Integer countByMonitoredUrl(ModelUrl monitoredUrl);
}
