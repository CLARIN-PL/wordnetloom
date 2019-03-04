package pl.edu.pwr.wordnetloom.client.ui.tooltip;

public class TooltipBuilder {

    private StringBuilder builder;
    private String separationString = ": ";
    private int level = 0;

    public TooltipBuilder(){
        builder = new StringBuilder();
    }

    public TooltipBuilder(String separationString){
        this.separationString = separationString;
    }

    public TooltipBuilder addTitle(String title){
        return add(title, "");
    }

    public TooltipBuilder add(String label, String value) {
        if(level != 0){
            addLevelSpace();
        }
        if(label == null){
            builder.append(value).append("\n");
        }else {
            builder.append(label).append(separationString).append(value).append("\n");
        }
        return this;
    }

    public TooltipBuilder append(String value){
        builder.append(value);
        return this;
    }

    private void addLevelSpace(){
        for(int i=0; i<level; i++){
            builder.append("\t");
        }
    }

    public TooltipBuilder add(String label, Integer value){
        return add(label, String.valueOf(value));
    }

    public TooltipBuilder setLevel(int level){
        this.level = level;
        return this;
    }

    public TooltipBuilder add(String label, String value, boolean condition){
        if(condition){
            return add(label, value);
        }
        return this;
    }

    public TooltipBuilder addValue(String value){
        if(level != 0){
            addLevelSpace();
        }
        builder.append(value).append("\n");
        return this;
    }

    public TooltipBuilder addNewLine(){
        builder.append("\n");
        return this;
    }

    public void clear(){
        builder.setLength(0);
    }

    @Override
    public String toString(){
        return builder.toString();
    }
}
