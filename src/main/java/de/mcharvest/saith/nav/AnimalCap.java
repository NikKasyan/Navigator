package de.mcharvest.saith.nav;

import de.mcharvest.saith.NavigatorPlugin;
import de.mcharvest.saith.nav.dijkstra.Vertex;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;

//This Class implements the ability to
//ride an Entity over a certain Path to
//a specific Location
public class AnimalCap {
    private static HashMap<Player, Integer> tasks = new HashMap<>();


    public static void rideEntityToDestination(Player p, Vertex[] path, Class<? extends LivingEntity> entityClazz) {
        LivingEntity entity = p.getWorld().spawn(p.getLocation(), entityClazz);
        entity.addPassenger(p);
        sendAnimal(p, path, entity);
    }

    private static void sendAnimal(Player p, Vertex[] path, LivingEntity entity) {
        if (entity instanceof CraftLivingEntity) {
            if (((CraftLivingEntity) entity).getHandle() instanceof EntityInsentient) {
                EntityInsentient entityInsentient = (EntityInsentient) ((CraftLivingEntity) entity).getHandle();
                Navigator.showPathToTravel(p, path, (vertices, adjacencyMatrix) -> {
                    int closest = Navigator.getClosestCheckpointIndex(p.getLocation(), vertices);
                    Location locClosest = vertices.get(closest).getLocation();
                    if (vertices.get(closest).getLocation().distance(p.getLocation()) <= 3) {
                        vertices.remove(vertices.get(closest));
                    }

                    entityInsentient.getNavigation().a(locClosest.getX(), locClosest.getY(), locClosest.getZ(), 1.0);

                }, () -> {
                    entity.remove();
                    p.sendMessage(NavigatorPlugin.getPrefix()+"§aYou arrived at your destination.");
                });
            }
        }
    }
}
