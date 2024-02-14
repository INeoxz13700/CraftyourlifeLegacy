package fr.innog.capability.playercapability;

import java.util.concurrent.Callable;

public class PlayerFactory implements Callable<IPlayer> {
    @Override
    public IPlayer call() throws Exception
    {
        return new PlayerData();
    }
}
