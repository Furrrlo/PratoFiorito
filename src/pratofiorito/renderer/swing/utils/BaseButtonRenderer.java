package pratofiorito.renderer.swing.utils;

import java.awt.AWTEvent;
import pratofiorito.renderer.swing.utils.borders.CustomBevelBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import pratofiorito.renderer.swing.SwingRenderManager;
import pratofiorito.renderer.swing.utils.borders.PartialLineBorder;

public abstract class BaseButtonRenderer extends JPanel {
    protected final SwingRenderManager manager;
    
    private Border lineBorder;
    private int oldLineBorderSize;
    
    private Border bevelBorder;
    private int oldBevelBorderSize;
    
    private boolean skipNextRepaint;
    
    private boolean isLeftClickDown;
    private boolean isRightClickDown;
    
    private boolean isPressed;
    private boolean isRightClicked;
    
    public BaseButtonRenderer(final SwingRenderManager manager) {
        this.manager = manager;
        
        new AWTMouseEventListener().register(true);
        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1)
                    setPressed(true);
                else if(e.getButton() == MouseEvent.BUTTON3)
                    setRightClicked(true);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1)
                    setPressed(false);
                else if(e.getButton() == MouseEvent.BUTTON3)
                    setRightClicked(false);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if(isLeftClickDown())
                    setPressed(true);
                else if(isRightClickDown())
                    setRightClicked(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setPressed(false);
                setRightClicked(false);
            }
        });
    }
    
    /*
     * This whole thing is a huge hack
     * RIP
     *
     * It exists so I can correctly emulate the behavior of the actual game.
     * Actions are done on mouse release on the cell that is being pointed,
     * while Java would generate the mouse release on the initial cell that
     * gets pressed.
     */
    private class AWTMouseEventListener implements AWTEventListener {
        private boolean isRegistered;
        
        public void register(boolean register) {
            this.isRegistered = register;
        
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            if(register)
                toolkit.addAWTEventListener(this, 
                        AWTEvent.MOUSE_EVENT_MASK | AWTEvent.CONTAINER_EVENT_MASK);
            else
                toolkit.removeAWTEventListener(this);
        }
        
        @Override
        public void eventDispatched(AWTEvent e) {
            if(!isRegistered)
                return;
            
            if(e instanceof MouseEvent)
                mouseEvent((MouseEvent)e);
            else if(e instanceof ContainerEvent)
                containerEvent((ContainerEvent)e);
        }
        
        private void mouseEvent(final MouseEvent e) {
            if(SwingUtilities.getRootPane((Component) e.getSource()) != getRootPane())
                return;
            
            switch(e.getID()) {
                case MouseEvent.MOUSE_CLICKED:
                    // Delete any clicked event not generated by me
                    if(!(e instanceof HackyMouseEvent))
                        e.consume();
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    if(e.getButton() == MouseEvent.BUTTON1)
                        setLeftClickDown(true);
                    else if(e.getButton() == MouseEvent.BUTTON3)
                        setRightClickDown(true);
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    boolean dispatchClickedEvent = false;
                    
                    if(e.getButton() == MouseEvent.BUTTON1) {
                        setLeftClickDown(false);

                        if(isPressed()) {
                            setPressed(false);
                            dispatchClickedEvent = true;
                        }
                    } else if(e.getButton() == MouseEvent.BUTTON3) {
                        setRightClickDown(false);

                        if(isRightClicked()) {
                            setRightClicked(false);
                            dispatchClickedEvent = true;
                        }
                    }
                    
                    if(dispatchClickedEvent) {
                        final Point p = SwingUtilities.convertPoint(
                                (Component) e.getSource(), 
                                e.getPoint(), 
                                BaseButtonRenderer.this);
                        dispatchEvent(new HackyMouseEvent(BaseButtonRenderer.this,
                                                     MouseEvent.MOUSE_CLICKED,
                                                     e.getWhen(),
                                                     e.getModifiers(),
                                                     p.x,
                                                     p.y,
                                                     e.getClickCount(),
                                                     e.isPopupTrigger()));
                        e.consume();
                    }
                    
                    break;
            }
        }
        
        private void containerEvent(final ContainerEvent e) {
            if(e.getID() == ContainerEvent.COMPONENT_REMOVED) {
                
                Component topLevelParent = BaseButtonRenderer.this;
                while(topLevelParent.getParent() != null)
                    topLevelParent = topLevelParent.getParent();
                
                if(e.getChild() == topLevelParent)
                    this.register(false);
            }
        }
        
        /*
         * This exists simply to distinguish the ones I generated 
         * from the actual ones that I want to delete
         */
        private class HackyMouseEvent extends MouseEvent {
            public HackyMouseEvent(Component source, int id, 
                                   long when, int modifiers, 
                                   int x, int y, int clickCount, 
                                   boolean popupTrigger, int button) {
                super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
            }

            public HackyMouseEvent(Component source, int id, 
                                   long when, int modifiers, 
                                   int x, int y, int clickCount, 
                                   boolean popupTrigger) {
                super(source, id, when, modifiers, x, y, clickCount, popupTrigger);
            }

            public HackyMouseEvent(Component source, int id, 
                                   long when, int modifiers, 
                                   int x, int y, int xAbs, 
                                   int yAbs, int clickCount, 
                                   boolean popupTrigger, int button) {
                super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger, button);
            }
            
        }
    }
    
    @Override
    public void repaint() {
        if(skipNextRepaint) {
           skipNextRepaint = false;
           return;
        }
        super.repaint();
    }

    protected void setBorderDuringPaint(Border border) {
        setSkipNextRepaint(true);
        this.setBorder(border);
        setSkipNextRepaint(false);
    }
    
    protected void setSkipNextRepaint(boolean skipNextRepaint) {
        this.skipNextRepaint = skipNextRepaint;
    }
            
    protected void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }
    
    protected boolean isPressed() {
        return isPressed;
    }
    
    protected void setLeftClickDown(boolean isLeftClickDown) {
        this.isLeftClickDown = isLeftClickDown;
    }
    
    protected boolean isLeftClickDown() {
        return isLeftClickDown;
    }
            
    protected void setRightClicked(boolean isRightClicked) {
        this.isRightClicked = isRightClicked;
    }
    
    protected boolean isRightClicked() {
        return isRightClicked;
    }
    
    protected void setRightClickDown(boolean isRightClickDown) {
        this.isRightClickDown = isRightClickDown;
    }
    
    protected boolean isRightClickDown() {
        return isRightClickDown;
    }
    
    protected Border getLineBorder() {
        final int lineBorderSize = Math.max(1, getSmallerDimension() / 16);
        if(lineBorderSize != oldLineBorderSize) {
            oldLineBorderSize = lineBorderSize;
            lineBorder = new PartialLineBorder(Color.GRAY, lineBorderSize,
                    PartialLineBorder.TOP_LINE | PartialLineBorder.LEFT_LINE);
        }
        return lineBorder;
    }
    
    protected Border getBevelBorder() {
        final int borderSize = Math.max(2, getSmallerDimension() / 8);
        if(oldBevelBorderSize != borderSize) {
            this.oldBevelBorderSize = borderSize;
            return (bevelBorder = new CustomBevelBorder(BevelBorder.RAISED, 
                            borderSize, 0,
                            Color.WHITE,
                            Color.GRAY));
        }
        return bevelBorder;
    }
    
    protected int getSmallerDimension() {
        return getWidth() < getHeight() ? getWidth() : getHeight();
    }
    
    protected void drawCenteredImage(final Graphics2D g2d, final Image img) {
        if(img == null)
            return;
        
        final int widthWOInsets = getWidth() - getInsets().left - getInsets().right;
        final int heightWOInsets = getHeight() - getInsets().top - getInsets().bottom;
        final int dim = widthWOInsets < heightWOInsets ? 
                widthWOInsets : 
                heightWOInsets;

        double toTranslate = 0;
        if (getWidth() < getHeight()) {
            toTranslate = (getHeight() - getWidth()) / 2;
            g2d.translate(0, toTranslate);
        } else if (getWidth() > getHeight()) {
            toTranslate = (getWidth() - getHeight()) / 2;
            g2d.translate(toTranslate, 0);
        }

        g2d.drawImage(img, getInsets().left, getInsets().top, dim, dim, this);

        if (getWidth() < getHeight()) {
            g2d.translate(0, -toTranslate);
        } else if (getWidth() > getHeight()) {
            g2d.translate(-toTranslate, 0);
        }
    }
}
