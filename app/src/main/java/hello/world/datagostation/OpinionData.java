package hello.world.datagostation;

public class OpinionData {
    private String userName;
    private String opinion;

    // 자바 기본 생성자
    // 기본 생성자가 있어야만 밑에 있는 것과 같이 파라미터를 받는 생성자 생성 가능
    public OpinionData() {}

    public OpinionData(String userName, String opinion) {
        this.userName = userName;
        this.opinion = opinion;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }
}
