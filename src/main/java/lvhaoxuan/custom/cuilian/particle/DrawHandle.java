package lvhaoxuan.custom.cuilian.particle;

import org.bukkit.Location;
import org.bukkit.Particle;

public class DrawHandle {
    public DrawHandle() {
    }

    public void onDraw(Location loc, Object data) {
    }

    public static class V19PlusDrawHandle extends DrawHandle {
        public V19PlusDrawHandle() {
        }

        public void onDraw(Location loc, Object data) {
            if (data instanceof Particle) {
                loc.getWorld().spawnParticle((Particle)data, loc, 0);
            }

        }
    }
}
