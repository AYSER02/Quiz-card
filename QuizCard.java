package quizcard;

/**
 *
 * @author ayser
 */
public class QuizCard {
    private String question;
    private String answer;

    public QuizCard(String f, String b){
        setQuestion(f);
        setAnswer(b);
    }
    String getAnswer(){
        return answer;
    }

    String getQuestion(){
        return question;
    }

    void setAnswer(String text){
        answer = text;
    }

    void setQuestion(String text){
        question = text;
    }
}