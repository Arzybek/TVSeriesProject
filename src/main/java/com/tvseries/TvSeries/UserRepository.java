package com.tvseries.TvSeries;

import com.tvseries.TvSeries.dto.TvShow;
import com.tvseries.TvSeries.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
}
