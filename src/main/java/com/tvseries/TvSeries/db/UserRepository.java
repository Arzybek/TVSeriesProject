package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
}
