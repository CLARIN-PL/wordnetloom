package pl.edu.pwr.wordnetloom.client.ui.tooltip;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ListTooltip extends Tooltip {

    private int delay = 1000;
    private int duration = Integer.MAX_VALUE;
    private int width = 500;

    public ListTooltip(){
        super();
        init();
    }

    public void setDelay(int delay){
        this.delay = delay;
        init();
    }

    public void setDuration(int duration){
        this.duration = duration;
        init();
    }

    public void setWidth(int width){
        this.width = width;
        setMaxWidth(width);
    }

    private void init(){
        setWrapText(true);
        setWidth(width);
        try {

            Class TTBehaviourClass = null;
            Class<?>[] declaredClasses = Tooltip.class.getDeclaredClasses();
            for (Class c:declaredClasses) {
                if (c.getCanonicalName().equals("javafx.scene.control.Tooltip.TooltipBehavior")) {
                    TTBehaviourClass = c;
                    break;
                }
            }
            if (TTBehaviourClass == null) {
                return;
            }
            Constructor constructor = TTBehaviourClass.getDeclaredConstructor(
                    Duration.class, Duration.class, Duration.class, boolean.class);
            if (constructor == null) {
                return;
            }
            constructor.setAccessible(true);
            Object newTTBehaviour = constructor.newInstance(
                    new Duration(delay), new Duration(duration),
                    new Duration(100), false);
            if (newTTBehaviour == null) {
                return;
            }
            Field ttbehaviourField = Tooltip.class.getDeclaredField("BEHAVIOR");
            if (ttbehaviourField == null) {
                return;
            }
            ttbehaviourField.setAccessible(true);

//            Object defaultTTBehavior = ttbehaviourField.get(Tooltip.class);
            ttbehaviourField.set(Tooltip.class, newTTBehaviour);

        } catch (Exception e) {
            System.out.println("Aborted setup due to error:" + e.getMessage());
        }
    }
}
