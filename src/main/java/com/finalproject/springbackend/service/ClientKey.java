package com.finalproject.springbackend.service;

import com.finalproject.springbackend.kafka.Region;

// KafkafConnectionManager에서 AdminClient를 사용자 + 지역으로 별도 구분해 캐싱하려면 Map<ClientKey, AdminClient> 형태 필요
// 근데 단순히 Map<String, AdminClient> 쓰면 username이 키로 쓰여서 같은 유저가 서울/오하이오 둘 다 쓸 때 충돌 문제가 생김
// 그래서 (Resion, username) 쌍을 하나로 묶는 전용 Key 클래스 필요
public record ClientKey(Region region, String username) { }
