package quiz;

import static util.JavalinUtil.app;

import exception.ApiError;
import exception.DaoException;

import java.util.List;
import java.util.Objects;

public class QuizApi {
    /**
     * This method is used to open the route for front-end to get the quiz statistics of
     * all the quizzes in a single file and send to the front-end
     * @param quizDao call quizDao to get data from quiz table
     */
    public static void getQuizStatByFileId(QuizDao quizDao) {
        // handle HTTP Get request to retrieve Quiz statistics
        app.get("/quizstat", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.queryParam("fileId"));
                System.out.println("File id: " + fileId);
                List<Quiz> quizzesByFileId = quizDao.getQuizStatByFileId(fileId);
                if (quizzesByFileId.isEmpty()) {
                    throw new ApiError("Unable to find quizzes", 500);
                }
                ctx.json(quizzesByFileId);
                ctx.status(200); // everything ok!
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500);
            }
        });
    }

    /**
     * This method is used to open the route for front-end to post a new quiz question
     * pass data to the Quiz class
     * call quizDao to add a quiz question to the database
     * @param quizDao call quizDao to update quiz table
     */
    public static void postQuiz(QuizDao quizDao) {
        // quizzes are initialized once a markdown in quiz format is uploaded
        app.post("/quiz", ctx -> {
            Quiz quiz = ctx.bodyAsClass(Quiz.class);
            try {
                quizDao.add(quiz);
                ctx.json(quiz);
                ctx.contentType("application/json");
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError("database error: " + ex.getMessage(), 500);
            } // quiz already exists, request forbidden
        });
    }

    /**
     * This method is used to open the route for front-end to post a record of a quiz question
     * pass data to the Record class
     * call quizDao to update the quiz table using the incoming record
     * @param quizDao call quizDao to update quiz table
     */
    public static void postRecords(QuizDao quizDao) {
        // student adds a record of a Quiz question through HTTP POST request
        app.post("/record", ctx -> {
            Record record = ctx.bodyAsClass(Record.class);
            try {
                quizDao.updateQuizStat(record);
                ctx.json(record);
                ctx.contentType("application/json");
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500);
            } // quiz not found
        });
    }
}
