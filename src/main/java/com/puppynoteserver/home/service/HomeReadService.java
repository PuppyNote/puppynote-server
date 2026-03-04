package com.puppynoteserver.home.service;

import com.puppynoteserver.home.service.response.HomeResponse;

public interface HomeReadService {

    HomeResponse getHomeInfo(Long petId);
}
