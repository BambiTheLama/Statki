public interface CommunicationInterface {

    String time="";
    ServerClientData data=new ServerClientData();

    default void startC()
    {

    }

    default void setAttackRes(int[] res, int size,int gold)
    {

    }

    default void setAttackPos(String[] pos, int size)
    {

    }

    default void setToSend(String send)
    {

    }

    default String[] getAttackPos()
    {
        return data.getAttackPos();
    }

    default int[] getAttackRes()
    {
        return data.getAttackRes();
    }

    default int getSize()
    {
        return data.getSize();
    }

    default boolean getWin()
    {
        return data.getWin();
    }

    default int getGold()
    {
        return data.getGold();
    }

    default String getTime(){return time;}

    default boolean isAttackStage(){return false;}

    default boolean getMyMove(){return false;}

    default void setMyMove(boolean move){}

    default void endComunication(){}

    default void setSzpieg(String pos,int gold) {}

    default boolean getSzpieg(){
        return data.getSzpieg();
    }

    default void end()
    {

    }

    default boolean getEnd()
    {
        return true;
    }

    default void setLost(){}

    default boolean getLost(){return false;}

    default boolean isAlive(){return false;}

    default void def(){}
}
