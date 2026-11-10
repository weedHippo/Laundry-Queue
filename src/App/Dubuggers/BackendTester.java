package App.Dubuggers;

import Service.DataManager.DataConnections;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Manual JDBC workbench. One connection is shared by all tabs and transaction controls. */
public final class BackendTester extends JFrame {
    private Connection connection;
    private Savepoint savepoint;
    private final JTextArea log = new JTextArea(7, 80);
    private final JLabel state = new JLabel("Not connected");
    private final List<Runnable> refreshers = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {} new BackendTester().setVisible(true); });
    }

    public BackendTester() {
        super("Laundry Queue - JDBC Backend Tester");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800); setMinimumSize(new Dimension(1000, 650)); setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() { public void windowClosing(java.awt.event.WindowEvent e) { closeWindow(); } });
        buildUi(); connect();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10)); root.setBorder(new EmptyBorder(12,12,12,12)); setContentPane(root);
        JPanel header = new JPanel(new BorderLayout(8, 6));
        JLabel title = new JLabel("Laundry Queue Backend Tester"); title.setFont(title.getFont().deriveFont(Font.BOLD, 20f)); header.add(title, BorderLayout.WEST);
        state.setForeground(new Color(0x995500)); header.add(state, BorderLayout.EAST);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        addButton(controls, "Create savepoint", e -> createSavepoint()); addButton(controls, "Rollback to savepoint", e -> rollbackToSavepoint());
        addButton(controls, "Rollback", e -> rollback()); addButton(controls, "Commit", e -> commit()); addButton(controls, "Refresh all", e -> refreshAll());
        header.add(controls, BorderLayout.SOUTH); root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(); tabs.addTab("Customers", customers()); tabs.addTab("Machines", machines());
        tabs.addTab("Loads", loads()); tabs.addTab("Assignments", assignments()); tabs.addTab("Services", services()); tabs.addTab("Other entities", otherEntities()); root.add(tabs, BorderLayout.CENTER);
        log.setEditable(false); log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); root.add(new JScrollPane(log), BorderLayout.SOUTH);
    }

    private JPanel customers() {
        JTextField id=field(), name=field(), contact=field(); JTable t=table();
        JPanel p=entityPanel(form("Customer ID (update/delete)",id,"Name",name,"Contact number",contact),t);
        JButton add=button("Add customer"), upd=button("Update selected ID"), del=button("Delete selected ID");
        add.addActionListener(e->run("Add customer",()->{ require(name,"Customer name"); exec("INSERT INTO CUSTOMER (CUSTOMER_NAME, CONTACT_NUMBER) VALUES (?, ?)",name.getText().trim(),empty(contact)); }));
        upd.addActionListener(e->run("Update customer",()->exec("UPDATE CUSTOMER SET CUSTOMER_NAME=?, CONTACT_NUMBER=? WHERE CUSTOMER_ID=?",name.getText().trim(),empty(contact),integer(id,"Customer ID"))));
        del.addActionListener(e->run("Delete customer",()->exec("DELETE FROM CUSTOMER WHERE CUSTOMER_ID=?",integer(id,"Customer ID"))));
        actions(p,add,upd,del); t.addMouseListener(select(t,r->{id.setText(v(t,r,0));name.setText(v(t,r,1));contact.setText(v(t,r,2));})); refresh(p,t,"SELECT CUSTOMER_ID,CUSTOMER_NAME,CONTACT_NUMBER FROM CUSTOMER ORDER BY CUSTOMER_ID"); return p;
    }

    private JPanel machines() {
        JTextField id=field(), capacity=field(); JComboBox<String> type=new JComboBox<>(new String[]{"WASHER","DRYER"}); JComboBox<String> status=new JComboBox<>(new String[]{"WORKING","MAINTENANCE","OUT_OF_SERVICE"}); JTable t=table();
        JPanel p=entityPanel(form("Machine ID (update/delete)",id,"Machine type",type,"Capacity (kg)",capacity,"Operational status",status),t);
        JButton add=button("Add machine"),upd=button("Update selected ID"),del=button("Delete selected ID");
        add.addActionListener(e->run("Add machine",()->exec("INSERT INTO MACHINES (MACHINE_TYPE,CAPACITY_KG,OPERATIONAL_STATUS) VALUES (?,?,?)",type.getSelectedItem(),decimal(capacity,"Capacity"),status.getSelectedItem())));
        upd.addActionListener(e->run("Update machine",()->exec("UPDATE MACHINES SET MACHINE_TYPE=?,CAPACITY_KG=?,OPERATIONAL_STATUS=? WHERE MACHINE_ID=?",type.getSelectedItem(),decimal(capacity,"Capacity"),status.getSelectedItem(),integer(id,"Machine ID"))));
        del.addActionListener(e->run("Delete machine",()->exec("DELETE FROM MACHINES WHERE MACHINE_ID=?",integer(id,"Machine ID"))));
        actions(p,add,upd,del); t.addMouseListener(select(t,r->{id.setText(v(t,r,0));type.setSelectedItem(v(t,r,1));capacity.setText(v(t,r,2));status.setSelectedItem(v(t,r,3));})); refresh(p,t,"SELECT MACHINE_ID,MACHINE_TYPE,CAPACITY_KG,OPERATIONAL_STATUS FROM MACHINES ORDER BY MACHINE_ID"); return p;
    }

    private JPanel loads() {
        JTextField customer=field(), weight=field(); JTable t=table(); JPanel p=entityPanel(form("Customer ID",customer,"Weight (kg)",weight),t); JButton add=button("Add load");
        add.addActionListener(e->run("Add load",()->exec("INSERT INTO LAUNDRY_LOADS (CUSTOMER_ID,WEIGHT_KG) VALUES (?,?)",integer(customer,"Customer ID"),decimal(weight,"Weight")))); actions(p,add); refresh(p,t,"SELECT LOAD_ID,CUSTOMER_ID,WEIGHT_KG,QUEUE_STATUS,RECEIVED_AT FROM LAUNDRY_LOADS ORDER BY LOAD_ID"); return p;
    }

    private JPanel assignments() {
        JTextField load=field(),machine=field(),duration=field(),endId=field(); JTable t=table(); JPanel p=entityPanel(form("Load ID",load,"Machine ID",machine,"Duration (minutes)",duration,"Assignment ID to end",endId),t); JButton add=button("Assign machine"),end=button("End assignment now");
        add.addActionListener(e->run("Assign machine",()->exec("INSERT INTO MACHINE_ASSIGNMENTS (LOAD_ID,MACHINE_ID,DURATION_MINUTES) VALUES (?,?,?)",integer(load,"Load ID"),integer(machine,"Machine ID"),integer(duration,"Duration"))));
        end.addActionListener(e->run("End assignment",()->exec("UPDATE MACHINE_ASSIGNMENTS SET ENDED_AT=CURRENT_TIMESTAMP WHERE ASSIGNMENT_ID=?",integer(endId,"Assignment ID")))); actions(p,add,end); refresh(p,t,"SELECT ASSIGNMENT_ID,LOAD_ID,MACHINE_ID,STARTED_AT,DURATION_MINUTES,ENDED_AT FROM MACHINE_ASSIGNMENTS ORDER BY ASSIGNMENT_ID"); return p;
    }

    private JPanel services() {
        JTextField id=field(),type=field(),price=field(); JTable t=table(); JPanel p=entityPanel(form("Service ID (update/delete)",id,"Service type",type,"Price",price),t); JButton add=button("Add service"),upd=button("Update selected ID"),del=button("Delete selected ID");
        add.addActionListener(e->run("Add service",()->exec("INSERT INTO SERVICES (SERVICE_TYPE,PRICE) VALUES (?,?)",require(type,"Service type"),decimal(price,"Price"))));
        upd.addActionListener(e->run("Update service",()->exec("UPDATE SERVICES SET SERVICE_TYPE=?,PRICE=? WHERE SERVICE_ID=?",require(type,"Service type"),decimal(price,"Price"),integer(id,"Service ID"))));
        del.addActionListener(e->run("Delete service",()->exec("DELETE FROM SERVICES WHERE SERVICE_ID=?",integer(id,"Service ID")))); actions(p,add,upd,del); t.addMouseListener(select(t,r->{id.setText(v(t,r,0));type.setText(v(t,r,1));price.setText(v(t,r,2));})); refresh(p,t,"SELECT SERVICE_ID,SERVICE_TYPE,PRICE FROM SERVICES ORDER BY SERVICE_ID"); return p;
    }

    private JPanel otherEntities() { JTable t=table(); JPanel p=new JPanel(new BorderLayout(8,8)); p.add(new JLabel("Read-only overview of every backend entity."),BorderLayout.NORTH); p.add(new JScrollPane(t),BorderLayout.CENTER); refresh(p,t,"SELECT 'CUSTOMER' ENTITY,COUNT(*) ROW_COUNT FROM CUSTOMER UNION ALL SELECT 'MACHINES',COUNT(*) FROM MACHINES UNION ALL SELECT 'LAUNDRY_LOADS',COUNT(*) FROM LAUNDRY_LOADS UNION ALL SELECT 'MACHINE_ASSIGNMENTS',COUNT(*) FROM MACHINE_ASSIGNMENTS UNION ALL SELECT 'SERVICES',COUNT(*) FROM SERVICES"); return p; }

    private JPanel entityPanel(JPanel form,JTable t){ JPanel p=new JPanel(new BorderLayout(8,8)); p.add(form,BorderLayout.NORTH); p.add(new JScrollPane(t),BorderLayout.CENTER); return p; }
    private void actions(JPanel p,JButton... bs){ JPanel a=new JPanel(new FlowLayout(FlowLayout.LEFT)); for(JButton b:bs)a.add(b); p.add(a,BorderLayout.SOUTH); }
    private JPanel form(Object... x){ JPanel p=new JPanel(new GridBagLayout()); GridBagConstraints c=new GridBagConstraints(); c.insets=new Insets(3,5,3,5); c.anchor=GridBagConstraints.WEST; for(int i=0;i<x.length;i+=2){c.gridx=0;c.gridy=i/2;p.add(new JLabel(String.valueOf(x[i])),c);c.gridx=1;p.add((Component)x[i+1],c);} return p; }
    private JTextField field(){return new JTextField(13);} private JButton button(String s){return new JButton(s);} private JTable table(){JTable t=new JTable(new DefaultTableModel());t.setAutoCreateRowSorter(true);t.setFillsViewportHeight(true);return t;}
    private void addButton(JPanel p,String s,java.awt.event.ActionListener l){JButton b=button(s);b.addActionListener(l);p.add(b);} private void actions(JPanel p,JButton b){actions(p,new JButton[]{b});}
    private void refresh(JPanel p,JTable t,String sql){refreshers.add(()->query(t,sql));SwingUtilities.invokeLater(()->query(t,sql));}
    private void refreshAll(){for(Runnable r:refreshers)try{r.run();}catch(Exception ignored){}}

    private void connect(){try{connection=DataConnections.getConnection();connection.setAutoCommit(false);state.setText("Connected - transaction open");log("Connected with auto-commit disabled.");refreshAll();}catch(SQLException e){error(e);}}
    private void createSavepoint(){try{savepoint=connection.setSavepoint("UI_SAVEPOINT");log("Savepoint created.");}catch(SQLException e){error(e);}}
    private void rollbackToSavepoint(){try{if(savepoint==null)throw new SQLException("Create a savepoint first.");connection.rollback(savepoint);log("Rolled back to savepoint.");refreshAll();}catch(SQLException e){error(e);}}
    private void rollback(){try{connection.rollback();savepoint=null;log("Transaction rolled back.");refreshAll();}catch(SQLException e){error(e);}}
    private void commit(){try{connection.commit();savepoint=null;log("Transaction committed; new transaction is open.");}catch(SQLException e){error(e);}}
    private void run(String action,SqlWork work){try{work.run();log(action+" succeeded (uncommitted).");refreshAll();}catch(Exception e){error(e);}}
    private void exec(String sql,Object... values)throws SQLException{try(PreparedStatement ps=connection.prepareStatement(sql)){for(int i=0;i<values.length;i++)ps.setObject(i+1,values[i]);log("  Rows affected: "+ps.executeUpdate());}}
    private void query(JTable t,String sql){if(connection==null)return;try(PreparedStatement ps=connection.prepareStatement(sql);ResultSet rs=ps.executeQuery()){ResultSetMetaData md=rs.getMetaData();DefaultTableModel m=new DefaultTableModel();for(int i=1;i<=md.getColumnCount();i++)m.addColumn(md.getColumnLabel(i));while(rs.next()){Object[] row=new Object[md.getColumnCount()];for(int i=0;i<row.length;i++)row[i]=rs.getObject(i+1);m.addRow(row);}t.setModel(m);t.setAutoCreateRowSorter(true);}catch(SQLException e){error(e);}}
    private void error(Exception e){log("ERROR: "+e.getMessage());state.setText("Database error");state.setForeground(new Color(0xAA0000));}
    private void log(String s){log.append(s+"\n");log.setCaretPosition(log.getDocument().getLength());}
    private int integer(JTextField f,String n){try{return Integer.parseInt(require(f,n));}catch(NumberFormatException e){throw new IllegalArgumentException(n+" must be a whole number.");}}
    private BigDecimal decimal(JTextField f,String n){try{return new BigDecimal(require(f,n));}catch(NumberFormatException e){throw new IllegalArgumentException(n+" must be a number.");}}
    private String require(JTextField f,String n){String s=f.getText().trim();if(s.isEmpty())throw new IllegalArgumentException(n+" is required.");return s;} private Object empty(JTextField f){String s=f.getText().trim();return s.isEmpty()?null:s;}
    private String v(JTable t,int r,int c){Object o=t.getValueAt(r,c);return o==null?"":String.valueOf(o);} private java.awt.event.MouseListener select(JTable t,java.util.function.IntConsumer c){return new java.awt.event.MouseAdapter(){public void mouseClicked(java.awt.event.MouseEvent e){int r=t.rowAtPoint(e.getPoint());if(r>=0)c.accept(t.convertRowIndexToModel(r));}};}
    private void closeWindow(){try{if(connection!=null&&!connection.isClosed()){connection.rollback();connection.close();}}catch(SQLException ignored){}dispose();} private interface SqlWork{void run()throws Exception;}
}
