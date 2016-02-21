package net.zomis.gameai;

public interface AfterMoveConsumer<E> {

    void afterMove(E game, Object saved, GameMove move);

}
