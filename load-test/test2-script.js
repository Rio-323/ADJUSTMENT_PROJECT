import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

export let errorCount = new Counter('errors');

const BASE_URL = 'http://localhost:8000';
const TOKEN = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlYjA4MDE3Mi01Nzc3LTQ2MGYtYjUzMS04NmY4MzcyMmVjZjIiLCJyb2xlIjoiQURNSU4iLCJleHAiOjE3MjE0Mzc2MDN9.REEqOhMzzSIaVb1kdGObC8j4tkzmHse-dzS3ZOApSM7FlCN_eJxXlTOT91SdMMemd7KD8lBC0AKBTgIrf55GGg';
const USER_ID = 'eb080172-5777-460f-b531-86f83722ecf2';

export let options = {
    stages: [
        { duration: '1m', target: 50 },
        { duration: '2m', target: 5000 },
        { duration: '1m', target: 0 },
    ],
};

export default function () {
    let headers = {
        'Authorization': `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
    };

    let params = {
        headers: headers,
        timeout: '300s',  // 5분으로 시간 제한을 늘림
    };

    // User service - 사용자 정보 조회
    let userRes = http.get(`${BASE_URL}/user-service/users/me`, params);
    check(userRes, { 'get user info status was 200': (r) => r.status === 200 });

    // Streaming service - 비디오 생성
    let createVideoRes = http.post(`${BASE_URL}/streaming-service/videos`, JSON.stringify({ title: 'New Video', url: 'http://example.com/video', duration: 120 }), params);
    check(createVideoRes, { 'create video status was 200': (r) => r.status === 200 });

    // Streaming service - 비디오 스트리밍 시작
    let playRes = http.post(`${BASE_URL}/streaming-service/play`, JSON.stringify({ videoId: 1, userId: USER_ID }), params);
    check(playRes, { 'play video status was 200': (r) => r.status === 200 });

    // Streaming service - 비디오 스트리밍 중지
    let pauseRes = http.post(`${BASE_URL}/streaming-service/pause`, JSON.stringify({ videoId: 1, userId: USER_ID, currentPosition: 10 }), params);
    check(pauseRes, { 'pause video status was 200': (r) => r.status === 200 });

    // 모든 비디오 조회 요청
    let getAllVideosRes = http.get(`${BASE_URL}/streaming-service/videos`, params);
    check(getAllVideosRes, { 'get all videos status was 200': (r) => r.status === 200 });

    // 광고 생성 요청
    let createAdRes = http.post(`${BASE_URL}/streaming-service/ads/create`, JSON.stringify({ videoId: 1, title: 'New Ad', url: 'http://example.com/ad' }), params);
    check(createAdRes, { 'create ad status was 200': (r) => r.status === 200 });

    // 모든 광고 조회 요청
    let getAllAdsRes = http.get(`${BASE_URL}/streaming-service/ads`, params);
    check(getAllAdsRes, { 'get all ads status was 200': (r) => r.status === 200 });

    // 광고 시청 요청
    let adWatchedRes = http.post(`${BASE_URL}/streaming-service/ads`, JSON.stringify({ adId: 1 }), params);
    check(adWatchedRes, { 'ad watched status was 200': (r) => r.status === 200 });

    // 특정 비디오와 광고의 전체 시청 수 조회 요청
    let getVideoAndAdCountsRes = http.get(`${BASE_URL}/streaming-service/counts?videoId=1&adId=1`, params);
    check(getVideoAndAdCountsRes, { 'get video and ad counts status was 200': (r) => r.status === 200 });

    // 특정 비디오의 일별 시청 수 조회 요청
    let getDailyVideoViewCountRes = http.get(`${BASE_URL}/streaming-service/videos/1/daily-views?date=2024-07-17`, params);
    check(getDailyVideoViewCountRes, { 'get daily video view count status was 200': (r) => r.status === 200 });

    // 특정 광고의 일별 시청 수 조회 요청
    let getDailyAdViewCountRes = http.get(`${BASE_URL}/streaming-service/ads/1/daily-views?date=2024-07-17`, params);
    check(getDailyAdViewCountRes, { 'get daily ad view count status was 200': (r) => r.status === 200 });

    sleep(1);
}