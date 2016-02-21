package net.zomis.gameai;

public class GameMove {

    private final AllowedCheck allowedCheck;
    private final Runnable perform;
    private final Object data;

    public static interface AllowedCheck {
        boolean test();
    }

    public GameMove(Object data, AllowedCheck allowedCheck, Runnable perform) {
        this.data = data;
        this.allowedCheck = allowedCheck;
        this.perform = perform;
    }

    public Object getData() {
        return data;
    }

    public boolean isAllowed() {
        return this.allowedCheck.test();
    }

    public void perform() {
        this.perform.run();
    }

}
