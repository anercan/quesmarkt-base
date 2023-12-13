package com.quesmarkt.quesmarktbase.service;

import com.quesmarkt.quesmarktbase.data.entity.Quiz;
import com.quesmarkt.quesmarktbase.data.entity.UserQuiz;
import com.quesmarkt.quesmarktbase.data.enums.UserQuizState;
import com.quesmarkt.quesmarktbase.data.mapper.QuizMapper;
import com.quesmarkt.quesmarktbase.data.mapper.UserQuizMapper;
import com.quesmarkt.quesmarktbase.data.request.QuizListWithUserDataRequest;
import com.quesmarkt.quesmarktbase.data.response.QuizListResponse;
import com.quesmarkt.quesmarktbase.data.response.QuizResponse;
import com.quesmarkt.quesmarktbase.data.response.QuizResponseWithUserData;
import com.quesmarkt.quesmarktbase.manager.QuizManager;
import com.quesmarkt.quesmarktbase.manager.UserQuizManager;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author anercan
 */

@Service
@AllArgsConstructor
public class QuizService {

    private final QuizManager quizManager;
    private final UserQuizManager userQuizManager;
    private final QuizMapper quizMapper;
    private final UserQuizMapper userQuizMapper;

    public ResponseEntity<QuizListResponse> getQuizListWithUserData(QuizListWithUserDataRequest request) {
        List<Quiz> quizList = quizManager.getQuizListWithGroupId(request);
        Map<Long, UserQuiz> quizIdUserQuizMap = userQuizManager.getQuizIdAndSolvedQuestionCountMap(request.getQuizGroupId());
        List<QuizResponseWithUserData> quizWithUserDataList = quizList.stream().map(quiz -> quizMapper.getQuizResponseWithUserData(quizIdUserQuizMap, quiz)).collect(Collectors.toList());
        return ResponseEntity.ok(QuizListResponse.builder().quizResponseWithUserDataList(quizWithUserDataList).build());
    }

    public ResponseEntity<QuizResponse> getQuizWithUserQuizDataForStartTest(Long quizId) {
        QuizResponse.QuizResponseBuilder<?, ?> quizResponseBuilder = QuizResponse.builder();
        Optional<UserQuiz> userQuizOptional = userQuizManager.getUserQuizWithQuizIdAndUserId(quizId);
        if (userQuizOptional.isPresent()) {
            UserQuiz userQuiz = userQuizOptional.get();
            if (UserQuizState.COMPLETED.equals(userQuiz.getState())) {
                quizResponseBuilder.userQuiz(userQuizMapper.toUserQuizResponse(userQuiz));
                return ResponseEntity.ok(quizResponseBuilder.build());
            } else {
                quizResponseBuilder.userQuiz(userQuizMapper.toUserQuizResponse(userQuiz));
            }
        }
        Optional<Quiz> quizWithId = quizManager.getQuizWithId(quizId);
        return quizWithId.map(quiz -> ResponseEntity.ok(quizMapper.toQuizResponse(quiz))).orElseGet(() -> ResponseEntity.internalServerError().build());
    }
}
