import org.junit.Test;

import java.util.Arrays;

public class RelationTest {

    @Test
    public void testRelationTestStringBuilding(){

        String test = "TAK: Jeśli ktoś/coś jest <x#subst:sg:inst:%>, to musi być <y#subst:sg:inst:%>";
        test = test.replace("<","[TAG]<");
        test = test.replace("%>","%>[TAG]");
        String[] split = test.split("\\[TAG\\]");

        Arrays.asList(split)
                .forEach(System.out::println);
    }
}
