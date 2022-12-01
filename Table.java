
package distancevectorproject;
import java.util.*;
import java.lang.Object;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author amirb
 */
public class Table {
    List<String[]> rowsList = new LinkedList<String[]>();
 
    private int[] columnWidth()
    {
        int cols = -1;
 
        for(String[] row : rowsList)
            cols = Math.max(cols, row.length);
 
        int[] widths = new int[cols];
 
        for(String[] row : rowsList) {
            for(int colNum = 0; colNum < row.length; colNum++) {
                widths[colNum] =
                    Math.max(
                        widths[colNum],
                        StringUtils.length(row[colNum]));
            }
        }
 
        return widths;
    }//columnWidth
    
    public void newRow(String... cols)
    {
        rowsList.add(cols);
    }//newRow
 
    @Override
    public String toString()
    {
        StringBuilder Mybuf = new StringBuilder();
 
        int[] widthCols = columnWidth();
        
        for(String[] row : rowsList) {
            for(int i = 0; i < row.length; i++) {
                Mybuf.append(StringUtils.rightPad(StringUtils.defaultString(row[i]), widthCols[i]));
                Mybuf.append(' ');
            }
 
            Mybuf.append('\n');
        }
 
        return Mybuf.toString();
    }//toString
    
    
}
