package gui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardPage extends JPanel implements ActionListener {
    public ImageIcon appIcon;
    private JButton changePage;
    private Runnable onChangePage;
    private JPanel topPanel;
    private JLabel headerLabel;

    public DashboardPage() {
        appIcon = new ImageIcon(
                getClass().getResource("/Icons/washer.png")
        );

        headerLabel = new JLabel("WELCOME");
        headerLabel.setFont(new Font("Sanserif",Font.BOLD,32));
        setLayout(new BorderLayout());
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        changePage = new JButton("Change Page");

        topPanel.add(changePage);
        add(headerLabel);
        add(topPanel,BorderLayout.NORTH);

        changePage.addActionListener(this);
    }

    public void setOnChangePage(Runnable action) {
        this.onChangePage = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == changePage) {
            if (onChangePage != null) {
                onChangePage.run();
            }
        }
    }
}