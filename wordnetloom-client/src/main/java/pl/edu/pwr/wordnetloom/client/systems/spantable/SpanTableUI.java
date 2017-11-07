package pl.edu.pwr.wordnetloom.client.systems.spantable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

/**
 * element interfejsu dla tabeli z połaczonymi komórkami
 *
 * @author Max
 */
public class SpanTableUI extends BasicTableUI {

    @Override
    public void paint(Graphics g, JComponent c) {
        Rectangle r = g.getClipBounds();
        int firstCol = table.columnAtPoint(new Point(r.x, 0));
        int lastCol = table.columnAtPoint(new Point(r.x + r.width, 0));
        if (lastCol < 0) {
            lastCol = table.getColumnCount() - 1;
        }
        for (int i = firstCol; i <= lastCol; i++) {
            paintCol(i, g);
        }
    }

    /**
     * narysowanie kolumny
     *
     * @param col - indeks kolumny
     * @param g - kontekst graficzny
     */
    private void paintCol(int col, Graphics g) {
        Rectangle rect = g.getClipBounds();
        SpanTable spanTable = (SpanTable) table;
        for (int row = 0; row < table.getRowCount(); row++) {
            Rectangle cellRect = table.getCellRect(row, col, true);
            if (cellRect.intersects(rect)) // czy jest widoczny
            {
                int rowIndex = spanTable.visibleRow(row, col);
                paintCell(rowIndex, col, g, cellRect);
                row += spanTable.rowSpan(rowIndex, col) - 1;
            }
        }
    }

    /**
     * narysowanie komorki
     *
     * @param row - wiersz
     * @param column - kolumna
     * @param g - kontekt graficzny
     * @param area - obszar do narysowania
     */
    private void paintCell(int row, int column, Graphics g, Rectangle area) {
        int verticalMargin = table.getRowMargin();
        int horizontalMargin = table.getColumnModel().getColumnMargin();

        Color oldColor = g.getColor();
        g.setColor(table.getGridColor());
        g.drawLine(area.x + area.width - 1, area.y, area.x + area.width - 1, area.y + area.height - 1);
        g.drawLine(area.x, area.y + area.height - 1, area.x + area.width - 1, area.y + area.height - 1);

        g.setColor(oldColor);

        area.setBounds(area.x + horizontalMargin / 2, area.y + verticalMargin
                / 2, area.width - horizontalMargin, area.height
                - verticalMargin);

        TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component component = table.prepareRenderer(renderer, row, column);
        if (component.getParent() == null) {
            rendererPane.add(component);
        }
        rendererPane.paintComponent(g, component, table, area.x, area.y, area.width, area.height, true);
    }
}