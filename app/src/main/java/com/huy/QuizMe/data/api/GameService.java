package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.game.GameResultDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Service interface cho các API liên quan đến game
 */
public interface GameService {

    /**
     * Bắt đầu trò chơi multiplayer
     *
     * @param roomId ID của phòng
     * @return Response từ server
     */
    @POST("api/game/rooms/{roomId}/start")
    Call<ApiResponse<Boolean>> startGame(@Path("roomId") Long roomId);

    /**
     * Lấy trạng thái hiện tại của game
     *
     * @param roomId ID của phòng
     * @return Trạng thái game hiện tại
     */
    @GET("api/game/rooms/{roomId}/status")
    Call<ApiResponse<String>> getGameStatus(@Path("roomId") Long roomId);

    /**
     * Lấy kết quả trò chơi
     *
     * @param roomId ID của phòng
     * @return Kết quả trò chơi
     */
    @GET("api/game/rooms/{roomId}/results")
    Call<ApiResponse<GameResultDTO>> getGameResults(@Path("roomId") Long roomId);
}
