package com.ssafy.idk.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.idk.domain.fcm.dto.request.FcmTokenRequestDto;
import com.ssafy.idk.domain.fcm.dto.response.FcmResponseDto;
import com.ssafy.idk.domain.fcm.exception.FcmException;
import com.ssafy.idk.domain.member.entity.Member;
import com.ssafy.idk.domain.member.repository.MemberRepository;
import com.ssafy.idk.domain.member.service.AuthenticationService;
import com.ssafy.idk.global.error.ErrorCode;
import com.ssafy.idk.global.util.FCMUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final AuthenticationService authenticationService;

    @Transactional
    public void saveToken(FcmTokenRequestDto requestDto) {
        Member member = authenticationService.getMemberByAuthentication();
        member.updateToken(requestDto.getToken());
    }

    @Transactional
    public void deleteToken() {
        Member member = authenticationService.getMemberByAuthentication();
        member.deleteToken();
    }

    public void sendNotification(FcmResponseDto fcmResponseDto) {
        if(fcmResponseDto.getToken() != null) {
            try {
                FCMUtil.init();

                Message message = Message.builder()
                        .setToken(fcmResponseDto.getToken())
                        .setNotification(Notification.builder()
                                .setTitle(fcmResponseDto.getTitle())
                                .setBody(fcmResponseDto.getBody())
                                .build())
                        .build();

                FirebaseMessaging.getInstance().sendAsync(message);

            } catch (Exception e) {
                throw new FcmException(ErrorCode.FCM_SEND_FAIL);
            }
        }
    }
}
