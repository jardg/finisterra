package ar.com.tamborindeguy.utils;

import ar.com.tamborindeguy.core.WorldServer;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import entity.Heading;
import physics.AOPhysics;
import position.WorldPos;

import java.util.ArrayList;
import java.util.List;

public class WorldUtils {

    public static int distance(WorldPos pos1, WorldPos pos2) {
        if (pos1.map != pos2.map) {
            return -1;
        }
        return Math.abs((pos1.x - pos2.x) + (pos1.y - pos2.y));
    }

    public static WorldPos getNextPos(WorldPos pos, AOPhysics.Movement movement) {
        return new WorldPos(
                (movement == AOPhysics.Movement.RIGHT ? 1 : movement == AOPhysics.Movement.LEFT ? -1 : 0) + pos.x,
                (movement == AOPhysics.Movement.UP ? -1 : movement == AOPhysics.Movement.DOWN ? 1 : 0) + pos.y,
                pos.map);
    }

    public static List<Component> getComponents(Entity player) {
        Bag<Component> components = player.getComponents(new Bag<>());
        List<Component> componentsToSend = new ArrayList<>();
        components.forEach(component -> {
            if (component != null) {
                componentsToSend.add(component);
            }
        });
        return componentsToSend;
    }

    public static List<Component> getComponents(int playerId) {
        return getComponents(WorldServer.getWorld().getEntity(playerId));
    }

    public static int getHeading(AOPhysics.Movement movement) {
        return movement == AOPhysics.Movement.UP ? Heading.HEADING_NORTH : movement == AOPhysics.Movement.DOWN ? Heading.HEADING_SOUTH : movement == AOPhysics.Movement.LEFT ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }
}
