package cz.bach.vsb.genetics.test.ngs;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestFoo {
    public static void main(String[] args) {
        String alignment = "(18,4666)(19,4665)(20,4664)(21,4663)(22,4662)(23,4661)(24,4660)(25,4659)(26,4658)(27,4657)(28,4656)(29,4656)(30,4655)(31,4654)(32,4653)(33,4652)";
        String regex = "\\((\\d+),(\\d+)\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(alignment);

        MatchResult result = matcher.toMatchResult();
        System.out.println("Matcher: " + result);

        while(matcher.find()) {
            System.out.printf("Ref Contig site id: %d, Qry Contig site id: %d\n",
                    new Integer(matcher.group(1)), new Integer(matcher.group(2)));
        }
    }
}
