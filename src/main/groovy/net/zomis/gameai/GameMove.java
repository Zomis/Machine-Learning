package net.zomis.gameai;

public class GameMove {

    private final AllowedCheck allowedCheck;
    private final Runnable perform;

    public static interface AllowedCheck {
        boolean test();
    }

    public GameMove(AllowedCheck allowedCheck, Runnable perform) {
        this.allowedCheck = allowedCheck;
        this.perform = perform;
    }

    public boolean isAllowed() {
        return this.allowedCheck.test();
    }

    public void perform() {
        this.perform.run();
    }

}
