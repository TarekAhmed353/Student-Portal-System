/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.studentportal;

/**
 *
 * @author pc
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Studentportal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.applyLight();
            new LoginFrame(user -> {
                if (user.role == Role.FACULTY) new FacultyDashboard(user).setVisible(true);
                else new StudentDashboard(user).setVisible(true);
            }).setVisible(true);
        });
    }
}

enum Role { FACULTY, STUDENT }

class Security {
    public static String sha256(char[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(new String(input).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}

class Clock {
    private static final ZoneId DHAKA = ZoneId.of("Asia/Dhaka");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static String now() { return ZonedDateTime.now(DHAKA).format(FMT); }
}

class Theme {
    public static Color BG = new Color(248, 250, 253);
    public static Color SURFACE = Color.WHITE;
    public static Color TEXT = new Color(35, 38, 47);
    public static Color PRIMARY = new Color(120, 140, 220);
    public static Color ACCENT = new Color(146, 180, 236);
    public static Color SUCCESS = new Color(121, 189, 168);
    public static Color WARNING = new Color(226, 160, 90);
    public static Color DANGER = new Color(227, 116, 116);
    public static Color PINK = new Color(232, 149, 183);
    public static Color PURPLE = new Color(162, 137, 226);
    public static Color TEAL = new Color(120, 184, 178);

    public static void applyLight() {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {}
        UIManager.put("control", BG);
        UIManager.put("info", SURFACE);
        UIManager.put("nimbusBase", new Color(110,120,190));
        UIManager.put("nimbusBlueGrey", new Color(190,195,220));
        UIManager.put("nimbusLightBackground", SURFACE);
        UIManager.put("text", TEXT);
        UIManager.put("nimbusSelectionBackground", PRIMARY.darker());
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("nimbusFocus", ACCENT);
        FontUIResource f = new FontUIResource("Segoe UI", Font.PLAIN, 14);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object val = UIManager.get(key);
            if (val instanceof FontUIResource) UIManager.put(key, f);
        }
    }

    public static void decorateFrame(JFrame f) {
        f.setIconImage(appImage());
        f.getContentPane().setBackground(BG);
    }

    public static void stylePanel(JComponent p) {
        p.setOpaque(true);
        p.setBackground(SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(232,236,248), 1, false),
                BorderFactory.createEmptyBorder(8,8,8,8)
        ));
    }

    public static void styleHeader(JLabel l) {
        l.setForeground(TEXT);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 18f));
    }

    public static void styleButton(AbstractButton b, Color base) {
        b.putClientProperty("baseColor", base);
        b.setUI(new RectButtonUI());
        b.setBackground(base);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(base.darker(), 1, false));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setIcon(null);
        b.setIconTextGap(8);
        b.setOpaque(true);
    }

    public static Icon tabIcon(String text, Color bg) {
        return new RectIcon(text, bg, Color.WHITE, 18, false);
    }

    public static Image appImage() {
        int s = 64;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0,0, PRIMARY, s, s, PINK);
        g.setPaint(gp);
        g.fillRect(0,0,s-1,s-1);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        String t = "MU";
        int tx = (s - fm.stringWidth(t))/2;
        int ty = (s + fm.getAscent()-fm.getDescent())/2;
        g.drawString(t, tx, ty);
        g.dispose();
        return img;
    }

    static class RectButtonUI extends BasicButtonUI {
        @Override public void installUI(JComponent c) {
            super.installUI(c);
            AbstractButton b = (AbstractButton) c;
            b.setContentAreaFilled(false);
            b.setOpaque(true);
        }
        @Override public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(b.getBackground());
            g2.fillRect(0, 0, c.getWidth(), c.getHeight());
            g2.dispose();
            super.paint(g, c);
        }
    }

    static class RectIcon implements Icon {
        final String text; final Color bg; final Color fg; final int size; final boolean rounded;
        RectIcon(String text, Color bg, Color fg, int size, boolean rounded){
            this.text=text; this.bg=bg; this.fg=fg; this.size=size; this.rounded=rounded;
        }
        public int getIconWidth(){ return size; }
        public int getIconHeight(){ return size; }
        public void paintIcon(Component c, Graphics g1, int x, int y){
            Graphics2D g=(Graphics2D)g1.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, rounded? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setColor(bg);
            if (rounded) g.fillRoundRect(x, y, size-1, size-1, 6, 6);
            else g.fillRect(x, y, size-1, size-1);
            g.setColor(fg);
            float fs = Math.max(10f, size*0.55f);
            g.setFont(c.getFont().deriveFont(Font.BOLD, fs));
            FontMetrics fm = g.getFontMetrics();
            int tx = x + (size - fm.stringWidth(text))/2;
            int ty = y + (size + fm.getAscent()-fm.getDescent())/2 - 1;
            g.drawString(text, tx, ty);
            g.dispose();
        }
    }
}

class User {
    final int id;
    final Role role;
    String username;
    String fullName;
    String passwordHash;
    String studentNumber;
    LocalDate dateOfBirth;
    String batchNumber;
    String major;
    User(int id, Role role, String username, String fullName, String passwordHash,
         String studentNumber, LocalDate dateOfBirth, String batchNumber, String major) {
        this.id=id; this.role=role; this.username=username; this.fullName=fullName;
        this.passwordHash=passwordHash; this.studentNumber=studentNumber;
        this.dateOfBirth=dateOfBirth; this.batchNumber=batchNumber; this.major=major;
    }
    @Override 
    public String toString() {
        if (role==Role.STUDENT && studentNumber!=null && !studentNumber.isEmpty())
            return fullName + " (" + username + ", ID: " + studentNumber + ")";
        return fullName + " (" + username + ")";
    }
}

class Course {
    final int id; final String code; final String title; final double credits;
    Course(int id, String code, String title, double credits){ this.id=id; this.code=code; this.title=title; this.credits=credits; }
    private String creditsStr() {
        return (Math.abs(credits - Math.rint(credits)) < 1e-9) ? String.valueOf((int)Math.rint(credits)) : String.valueOf(credits);
    }
    @Override
    public String toString(){
        return code + " - " + title + " (" + creditsStr() + " credits)";
    }
}

class Enrollment {
    final int id, studentId, courseId; final String semester;
    Enrollment(int id,int studentId,int courseId,String semester){ this.id=id; this.studentId=studentId; this.courseId=courseId; this.semester=semester; }
}

class Announcement {
    final int id; final String title, body, createdAt;
    Announcement(int id,String title,String body,String createdAt){ this.id=id; this.title=title; this.body=body; this.createdAt=createdAt; }
    @Override public String toString(){ return createdAt+" | "+title+" — "+body; }
}

class Grade {
    final int id, studentId, courseId;
    int marks; double credits; String letter; double points;
    Grade(int id,int studentId,int courseId,int marks,double credits,String letter,double points){
        this.id=id; this.studentId=studentId; this.courseId=courseId;
        this.marks=marks; this.credits=credits; this.letter=letter; this.points=points;
    }
    @Override public String toString(){
        return "Course "+courseId+" — "+letter+"("+points+"), marks="+marks+", credits="+credits;
    }
}

class Feedback {
    final int id; final Integer studentId; final String facultyName; final String message; final String createdAt;
    Feedback(int id,Integer studentId,String facultyName,String message,String createdAt){
        this.id=id; this.studentId=studentId;
        this.facultyName = (facultyName==null? "" : facultyName.trim());
        this.message=message; this.createdAt=createdAt;
    }
    @Override public String toString(){
        String to = facultyName==null || facultyName.isEmpty()? "" : (" | To: " + facultyName);
        return createdAt + to + " | " + message;
    }
}

class LostItem {
    final int id; final Integer reporterId; final String itemName, description, createdAt;
    String status;
    LostItem(int id,Integer reporterId,String itemName,String description,String status,String createdAt){
        this.id=id; this.reporterId=reporterId; this.itemName=itemName; this.description=description; this.status=status; this.createdAt=createdAt;
    }
    @Override public String toString(){ return createdAt+" | "+itemName+" ("+status+") - "+description; }
}

class UserRepo {
    private static int seq = 1;
    private static final java.util.List<User> users = new ArrayList<>();
    private static final DateTimeFormatter DOB_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static { add(seedFaculty("faculty","Faculty Member","faculty")); }

    private static User seedFaculty(String username,String fullName,String rawPass){
        String hash = Security.sha256(rawPass.toCharArray());
        return new User(seq++, Role.FACULTY, username, fullName, hash, null, null, null, null);
    }
    private static void add(User u){ users.add(u); }

    public static User findByUsername(String username){ for(User u:users) if(u.username.equals(username)) return u; return null; }
    public static User findById(int id){ for(User u:users) if(u.id==id) return u; return null; }
    public static java.util.List<User> students(){ java.util.List<User> out=new ArrayList<>(); for(User u:users) if(u.role==Role.STUDENT) out.add(u); return out; }

    public static User createStudent(String fullName,String username,char[] password,
                                     String studentNumber,String dobString,String batchNumber,String major){
        try{
            if(fullName==null||fullName.isBlank()||username==null||username.isBlank()
               ||studentNumber==null||studentNumber.isBlank()
               ||dobString==null||dobString.isBlank()
               ||batchNumber==null||batchNumber.isBlank()
               ||major==null||major.isBlank()) return null;

            LocalDate dob; try{ dob=LocalDate.parse(dobString.trim(),DOB_FMT);}catch(Exception e){ return null; }

            for(User u:users){
                if(u.username.equalsIgnoreCase(username)) return null;
                if(u.role==Role.STUDENT && u.studentNumber!=null && !u.studentNumber.isBlank()
                        && u.studentNumber.equalsIgnoreCase(studentNumber)) return null;
            }
            String hash = Security.sha256(password);
            User u = new User(seq++, Role.STUDENT, username.trim(), fullName.trim(), hash,
                    studentNumber.trim(), dob, batchNumber.trim(), major.trim());
            users.add(u);
            return u;
        } finally { if(password!=null) Arrays.fill(password,'\0'); }
    }

    public static User updateStudent(int id,String fullName,String username,char[] newPassword,
                                     String studentNumber,String dobString,String batchNumber,String major){
        User u=findById(id); if(u==null||u.role!=Role.STUDENT) return null;
        try{
            if(fullName==null||fullName.isBlank()||username==null||username.isBlank()
               ||studentNumber==null||studentNumber.isBlank()
               ||dobString==null||dobString.isBlank()
               ||batchNumber==null||batchNumber.isBlank()
               ||major==null||major.isBlank()) return null;

            LocalDate dob; try{ dob=LocalDate.parse(dobString.trim(),DOB_FMT);}catch(Exception e){ return null; }

            for(User o:users){
                if(o.id!=id){
                    if(o.username.equalsIgnoreCase(username)) return null;
                    if(o.role==Role.STUDENT && o.studentNumber!=null && !o.studentNumber.isBlank()
                            && o.studentNumber.equalsIgnoreCase(studentNumber)) return null;
                }
            }
            u.fullName=fullName.trim(); u.username=username.trim();
            if(newPassword!=null && newPassword.length>0) u.passwordHash=Security.sha256(newPassword);
            u.studentNumber=studentNumber.trim(); u.dateOfBirth=dob;
            u.batchNumber=batchNumber.trim(); u.major=major.trim();
            return u;
        } finally { if(newPassword!=null) Arrays.fill(newPassword,'\0'); }
    }

    public static boolean deleteStudent(int id){
        Iterator<User> it=users.iterator();
        while(it.hasNext()){ User u=it.next(); if(u.id==id && u.role==Role.STUDENT){ it.remove(); return true; } }
        return false;
    }
}

class CourseRepo {
    private static int seq = 1;
    private static final java.util.List<Course> courses = new ArrayList<>();

    public static java.util.List<Course> list(){ return Collections.unmodifiableList(courses); }
    public static Course create(String code,String title,double credits){ Course c=new Course(seq++,code,title,credits); courses.add(c); return c; }
    public static Course findById(int id){ for(Course c:courses) if(c.id==id) return c; return null; }
}

class EnrollmentRepo {
    private static int seq = 1;
    private static final java.util.List<Enrollment> enrollments = new ArrayList<>();
    public static boolean enroll(int studentId,int courseId,String semester){
        for(Enrollment e:enrollments)
            if(e.studentId==studentId && e.courseId==courseId && e.semester.equalsIgnoreCase(semester)) return false;
        enrollments.add(new Enrollment(seq++,studentId,courseId,semester)); return true;
    }
    public static java.util.List<Enrollment> byStudent(int studentId){
        java.util.List<Enrollment> out=new ArrayList<>(); for(Enrollment e:enrollments) if(e.studentId==studentId) out.add(e); return out;
    }
}

class AnnouncementRepo {
    private static int seq = 1;
    private static final java.util.List<Announcement> items = new ArrayList<>();
    public static void create(String title,String body){ items.add(new Announcement(seq++,title,body,Clock.now())); }
    public static java.util.List<Announcement> list(){ java.util.List<Announcement> copy=new ArrayList<>(items); Collections.reverse(copy); return copy; }
    public static void delete(int id){ for(Iterator<Announcement> it=items.iterator(); it.hasNext();){ Announcement a=it.next(); if(a.id==id){ it.remove(); return; } } }
}

class GradeRepo {
    private static int seq = 1;
    private static final java.util.List<Grade> grades = new ArrayList<>();

    public static String letterFromMarks(int m){
        if(m>=80) return "A+";
        if(m>=75) return "A";
        if(m>=70) return "A-";
        if(m>=65) return "B+";
        if(m>=60) return "B";
        if(m>=55) return "B-";
        if(m>=50) return "C+";
        if(m>=45) return "C";
        if(m>=40) return "D";
        return "F";
    }
    public static double pointsFromMarks(int m){
        if(m>=80) return 4.00;
        if(m>=75) return 3.75;
        if(m>=70) return 3.50;
        if(m>=65) return 3.25;
        if(m>=60) return 3.00;
        if(m>=55) return 2.75;
        if(m>=50) return 2.50;
        if(m>=45) return 2.25;
        if(m>=40) return 2.00;
        return 0.00;
    }

    public static void assignFromMarks(int studentId,int courseId,int marks,double credits){
        marks=Math.max(0,Math.min(100,marks));
        String L=letterFromMarks(marks); double P=pointsFromMarks(marks);
        for(Grade g:grades){
            if(g.studentId==studentId && g.courseId==courseId){
                g.marks=marks; g.credits=credits; g.letter=L; g.points=P; return;
            }
        }
        grades.add(new Grade(seq++,studentId,courseId,marks,credits,L,P));
    }

    public static java.util.List<Grade> byStudent(int studentId){
        java.util.List<Grade> out=new ArrayList<>(); for(Grade g:grades) if(g.studentId==studentId) out.add(g); return out;
    }
}

class FeedbackRepo {
    private static int seq = 1;
    private static final java.util.List<Feedback> items = new ArrayList<>();

    public static void submit(Integer studentId, String facultyName, String message){
        if(message==null || message.trim().isEmpty()) return;
        items.add(new Feedback(seq++,studentId,facultyName,message.trim(),Clock.now()));
    }
    public static java.util.List<Feedback> list(){
        java.util.List<Feedback> copy=new ArrayList<>(items);
        Collections.reverse(copy);
        return copy;
    }
}

class LostItemRepo {
    private static int seq = 1;
    private static final java.util.List<LostItem> items = new ArrayList<>();

    public static void report(Integer reporterId,String itemName,String description){
        if(itemName==null || itemName.trim().isEmpty()) return;
        items.add(new LostItem(seq++, reporterId, itemName.trim(),
                description==null? "": description.trim(), "LOST", Clock.now()));
    }
    public static void markClaimed(int id){
        for(LostItem li:items) if(li.id==id && "LOST".equals(li.status)){ li.status="CLAIMED"; return; }
    }
    public static java.util.List<LostItem> list(){ java.util.List<LostItem> copy=new ArrayList<>(items); Collections.reverse(copy); return copy; }
}

class LoginFrame extends JFrame {
    private final JTextField username = new JTextField(15);
    private final JPasswordField password = new JPasswordField(15);
    private final JButton studentBtn = new JButton("Student Login");
    private final JButton facultyBtn = new JButton("Faculty Login");

    interface LoginSuccess { void onLogin(User user); }

    public LoginFrame(LoginSuccess cb) {
        setTitle("METROPOLITAN UNIVERSITY BANGLADESH STUDENT PORTAL");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 340);
        setLocationRelativeTo(null);
        Theme.decorateFrame(this);

        JLabel header = new JLabel("METROPOLITAN UNIVERSITY BANGLADESH STUDENT PORTAL", SwingConstants.CENTER);
        Theme.styleHeader(header);
        header.setBorder(BorderFactory.createEmptyBorder(16,16,4,16));

        JPanel form = new JPanel(new GridBagLayout());
        Theme.stylePanel(form);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8); g.fill = GridBagConstraints.HORIZONTAL;

        JLabel uLbl=new JLabel("Username"); uLbl.setForeground(Theme.TEXT);
        JLabel pLbl=new JLabel("Password"); pLbl.setForeground(Theme.TEXT);

        password.setEchoChar('\u25CF');
        password.setFont(password.getFont().deriveFont(16f));

        g.gridx=0; g.gridy=0; form.add(uLbl, g);
        g.gridx=1; form.add(username, g);
        g.gridx=0; g.gridy=1; form.add(pLbl, g);
        g.gridx=1; form.add(password, g);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        buttons.setOpaque(false);
        Theme.styleButton(studentBtn, Theme.PRIMARY);
        Theme.styleButton(facultyBtn, Theme.PURPLE);
        buttons.add(studentBtn); buttons.add(facultyBtn);
        g.gridx=0; g.gridy=2; g.gridwidth=2; form.add(buttons, g);

        JPanel root = new JPanel(new BorderLayout(12,12));
        root.setBackground(Theme.BG);
        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        add(root);

        studentBtn.addActionListener(e -> doLogin(Role.STUDENT, cb));
        facultyBtn.addActionListener(e -> doLogin(Role.FACULTY, cb));
    }

    private void doLogin(Role expectedRole, LoginSuccess cb) {
        User u = UserRepo.findByUsername(username.getText().trim());
        if (u == null) { JOptionPane.showMessageDialog(this, "User not found"); return; }
        char[] pass = password.getPassword();
        try {
            String hash = Security.sha256(pass);
            Arrays.fill(pass, '\0');
            if (!u.passwordHash.equals(hash)) { JOptionPane.showMessageDialog(this, "Invalid password"); return; }
        } finally { Arrays.fill(pass, '\0'); }
        if (u.role != expectedRole) { JOptionPane.showMessageDialog(this, "Use the correct login for your role."); return; }
        cb.onLogin(u); dispose();
    }
}

class FacultyDashboard extends JFrame {
    private final DefaultComboBoxModel<User> gradeStudentsModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<Course> gradeCoursesModel = new DefaultComboBoxModel<>();

    public FacultyDashboard(User faculty) {
        setTitle("Faculty Dashboard - " + faculty.fullName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);
        Theme.decorateFrame(this);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Theme.SURFACE);
        tabs.addTab("Announcements", Theme.tabIcon("An", Theme.PINK), announcementPanel());
        tabs.addTab("Courses", Theme.tabIcon("Co", Theme.ACCENT), coursePanel());
        tabs.addTab("Student Records", Theme.tabIcon("SR", Theme.SUCCESS), studentRecordsPanel());
        tabs.addTab("Grades", Theme.tabIcon("Gr", Theme.PURPLE), gradePanel());
        tabs.addTab("Lost & Found", Theme.tabIcon("LF", Theme.WARNING), lostFoundPanel());
        tabs.addTab("Feedback (All)", Theme.tabIcon("FB", Theme.TEAL), feedbackPanel());
        add(tabs, BorderLayout.CENTER);

        JButton logout = new JButton("Logout");
        Theme.styleButton(logout, Theme.DANGER);
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame(user -> {
                    if (user.role == Role.FACULTY) new FacultyDashboard(user).setVisible(true);
                    else new StudentDashboard(user).setVisible(true);
                }).setVisible(true);
            }
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(Theme.BG);
        bottom.add(logout);
        add(bottom, BorderLayout.SOUTH);

        refreshGradeCombos();
    }

    private JPanel announcementPanel() {
        JPanel p = new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        JPanel composer = new JPanel(new GridBagLayout());
        Theme.stylePanel(composer);

        JTextField titleField = new JTextField(36);
        JTextArea body = new JTextArea(6, 50);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);

        JButton post = new JButton("Post Announcement");
        JButton del  = new JButton("Delete Selected");
        Theme.styleButton(post, Theme.PRIMARY);
        Theme.styleButton(del, Theme.DANGER);

        DefaultListModel<Announcement> model = new DefaultListModel<>();
        JList<Announcement> list = new JList<>(model);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel tLbl=new JLabel("Title"); tLbl.setForeground(Theme.TEXT);
        JLabel bLbl=new JLabel("Body");  bLbl.setForeground(Theme.TEXT);

        g.gridx=0; g.gridy=0; g.weightx=0; composer.add(tLbl, g);
        g.gridx=1; g.gridy=0; g.weightx=1.0; composer.add(titleField, g);
        g.gridx=0; g.gridy=1; g.weightx=0; composer.add(bLbl, g);
        g.gridx=1; g.gridy=1; g.weightx=1.0;
        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setPreferredSize(new Dimension(0, 140));
        composer.add(bodyScroll, g);
        g.gridx=1; g.gridy=2; g.weightx=0; composer.add(post, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(del);

        Theme.stylePanel(list);
        p.add(composer, BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        p.add(actions, BorderLayout.SOUTH);

        post.addActionListener(e -> {
            String t = titleField.getText().trim();
            String b = body.getText().trim();
            if (t.isEmpty() || b.isEmpty()) { JOptionPane.showMessageDialog(this, "Title and body required"); return; }
            AnnouncementRepo.create(t, b);
            titleField.setText(""); body.setText("");
            refreshAnnouncements(model);
        });

        del.addActionListener(e -> {
            Announcement sel = list.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select an announcement to delete."); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this announcement?", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
            if (confirm == JOptionPane.OK_OPTION) {
                AnnouncementRepo.delete(sel.id);
                refreshAnnouncements(model);
            }
        });

        refreshAnnouncements(model);
        return p;
    }
    private void refreshAnnouncements(DefaultListModel<Announcement> model) {
        model.clear();
        for (Announcement a : AnnouncementRepo.list()) model.addElement(a);
    }

    private JPanel coursePanel() {
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        DefaultListModel<Course> model=new DefaultListModel<>();
        JList<Course> list=new JList<>(model);
        Theme.stylePanel(list);

        JPanel top=new JPanel(new GridLayout(0,2,8,8));
        Theme.stylePanel(top);

        JTextField code=new JTextField(), title=new JTextField();
        JComboBox<Double> creditsBox = new JComboBox<>(new Double[]{3.0, 1.5});
        JButton add=new JButton("Add Course");
        Theme.styleButton(add, Theme.SUCCESS);

        top.add(new JLabel("Code")); top.add(code);
        top.add(new JLabel("Title")); top.add(title);
        top.add(new JLabel("Credits")); top.add(creditsBox);
        top.add(new JLabel()); top.add(add);

        add.addActionListener(e->{
            String c=code.getText().trim(), t=title.getText().trim();
            if(c.isEmpty()||t.isEmpty()){ JOptionPane.showMessageDialog(this,"Code & Title required"); return; }
            Double cr=(Double)creditsBox.getSelectedItem(); if(cr==null) cr=3.0;
            CourseRepo.create(c,t,cr);
            refreshCourses(model); code.setText(""); title.setText(""); creditsBox.setSelectedIndex(0); refreshGradeCombos();
        });

        p.add(top,BorderLayout.NORTH); p.add(new JScrollPane(list),BorderLayout.CENTER); refreshCourses(model); return p;
    }
    private void refreshCourses(DefaultListModel<Course> model){ model.clear(); for(Course c:CourseRepo.list()) model.addElement(c); }

    private JPanel studentRecordsPanel() {
        JPanel p = new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        JPanel form=new JPanel(new GridBagLayout());
        Theme.stylePanel(form);

        JTextField fullName=new JTextField(), studentIdNum=new JTextField(),
                   batch=new JTextField(), major=new JTextField(), username=new JTextField();
        JPasswordField password=new JPasswordField();
        password.setEchoChar('\u25CF');
        password.setFont(password.getFont().deriveFont(16f));

        JComboBox<Integer> dayBox = new JComboBox<>();
        for(int d=1; d<=31; d++) dayBox.addItem(d);
        JComboBox<String> monthBox = new JComboBox<>(new String[]{
                "January","February","March","April","May","June","July","August","September","October","November","December"
        });
        int currentYear = LocalDate.now().getYear();
        java.util.List<Integer> years = new ArrayList<>();
        for(int y=1990; y<=currentYear; y++) years.add(y);
        JComboBox<Integer> yearBox = new JComboBox<>(years.toArray(new Integer[0]));
        dayBox.setPrototypeDisplayValue(30);
        monthBox.setPrototypeDisplayValue("September");
        yearBox.setPrototypeDisplayValue(1990);
        Dimension h = new Dimension(72, dayBox.getPreferredSize().height);
        dayBox.setPreferredSize(h);
        monthBox.setPreferredSize(new Dimension(120, h.height));
        yearBox.setPreferredSize(new Dimension(90, h.height));
        LocalDate today = LocalDate.now();
        dayBox.setSelectedItem(today.getDayOfMonth());
        monthBox.setSelectedIndex(today.getMonthValue()-1);
        yearBox.setSelectedItem(today.getYear());

        Runnable syncDays = () -> {
            int y = (Integer)yearBox.getSelectedItem();
            int m = monthBox.getSelectedIndex()+1;
            int max = Month.of(m).length(Year.isLeap(y));
            int cur = (Integer)dayBox.getSelectedItem();
            dayBox.removeAllItems();
            for(int d=1; d<=max; d++) dayBox.addItem(d);
            if (cur<=max) dayBox.setSelectedItem(cur);
            else dayBox.setSelectedItem(max);
        };
        yearBox.addActionListener(e -> syncDays.run());
        monthBox.addActionListener(e -> syncDays.run());

        JButton add=new JButton("Add Student"), update=new JButton("Update Selected"),
                del=new JButton("Delete Selected"), clear=new JButton("Clear");
        Theme.styleButton(add, Theme.SUCCESS);
        Theme.styleButton(update, Theme.ACCENT);
        Theme.styleButton(del, Theme.DANGER);
        Theme.styleButton(clear, Theme.WARNING);

        String[] cols={"_ID","Name","Student ID","DOB","Batch","Department","Username"};
        DefaultTableModel tm=new DefaultTableModel(cols,0){ @Override public boolean isCellEditable(int r,int c){ return false; } };
        JTable table=new JTable(tm); table.setFillsViewportHeight(true);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        table.getSelectionModel().addListSelectionListener(e->{
            if(e.getValueIsAdjusting()) return;
            int r=table.getSelectedRow(); if(r<0) return;
            fullName.setText(s(tm.getValueAt(r,1)));
            studentIdNum.setText(s(tm.getValueAt(r,2)));
            String dobStr = s(tm.getValueAt(r,3));
            if(!dobStr.isEmpty()){
                try{
                    LocalDate dob = LocalDate.parse(dobStr);
                    dayBox.setSelectedItem(dob.getDayOfMonth());
                    monthBox.setSelectedIndex(dob.getMonthValue()-1);
                    yearBox.setSelectedItem(dob.getYear());
                }catch(Exception ignored){}
            }
            batch.setText(s(tm.getValueAt(r,4)));
            major.setText(s(tm.getValueAt(r,5)));
            username.setText(s(tm.getValueAt(r,6)));
            password.setText("");
        });

        add.addActionListener(e->{
            String fn=fullName.getText().trim(), sn=studentIdNum.getText().trim(),
                   bn=batch.getText().trim(), mj=major.getText().trim(), un=username.getText().trim();
            char[] pw=password.getPassword();
            Integer d=(Integer)dayBox.getSelectedItem();
            int m=monthBox.getSelectedIndex()+1;
            Integer y=(Integer)yearBox.getSelectedItem();
            if(fn.isEmpty()||sn.isEmpty()||bn.isEmpty()||mj.isEmpty()||un.isEmpty()||pw.length==0){
                JOptionPane.showMessageDialog(this,"All fields are required (including Password)"); return;
            }
            if(d==null||y==null){ JOptionPane.showMessageDialog(this,"Invalid date"); return; }
            int max = Month.of(m).length(Year.isLeap(y));
            if(d>max){ JOptionPane.showMessageDialog(this,"Invalid date for selected month/year"); return; }
            String dobStr = String.format("%04d-%02d-%02d", y, m, d);
            User created=UserRepo.createStudent(fn,un,pw,sn,dobStr,bn,mj);
            if(created==null){ JOptionPane.showMessageDialog(this,"Invalid input or duplicate Username/Student ID."); return; }
            clearForm(fullName,studentIdNum,batch,major,username,password);
            dayBox.setSelectedItem(today.getDayOfMonth());
            monthBox.setSelectedIndex(today.getMonthValue()-1);
            yearBox.setSelectedItem(today.getYear());
            refreshStudentTable(tm); refreshGradeCombos();
            JOptionPane.showMessageDialog(this,"Student added: "+created.fullName);
        });

        update.addActionListener(e->{
            int r=table.getSelectedRow(); if(r<0){ JOptionPane.showMessageDialog(this,"Select a row to update."); return; }
            int id=Integer.parseInt(s(tm.getValueAt(r,0)));
            String fn=fullName.getText().trim(), sn=studentIdNum.getText().trim(),
                   bn=batch.getText().trim(), mj=major.getText().trim(), un=username.getText().trim();
            char[] pw=password.getPassword();
            Integer d=(Integer)dayBox.getSelectedItem();
            int m=monthBox.getSelectedIndex()+1;
            Integer y=(Integer)yearBox.getSelectedItem();
            if(fn.isEmpty()||sn.isEmpty()||bn.isEmpty()||mj.isEmpty()||un.isEmpty()){
                JOptionPane.showMessageDialog(this,"All fields are required (password can be blank for no change)"); return;
            }
            int max = Month.of(m).length(Year.isLeap(y));
            if(d>max){ JOptionPane.showMessageDialog(this,"Invalid date for selected month/year"); return; }
            String dobStr = String.format("%04d-%02d-%02d", y, m, d);
            User updated=UserRepo.updateStudent(id,fn,un,pw,sn,dobStr,bn,mj);
            if(updated==null){ JOptionPane.showMessageDialog(this,"Update failed. Check inputs & uniqueness."); return; }
            clearForm(fullName,studentIdNum,batch,major,username,password);
            dayBox.setSelectedItem(today.getDayOfMonth());
            monthBox.setSelectedIndex(today.getMonthValue()-1);
            yearBox.setSelectedItem(today.getYear());
            refreshStudentTable(tm); refreshGradeCombos();
            JOptionPane.showMessageDialog(this,"Student updated.");
        });

        del.addActionListener(e->{
            int r=table.getSelectedRow(); if(r<0){ JOptionPane.showMessageDialog(this,"Select a row to delete."); return; }
            int id=Integer.parseInt(s(tm.getValueAt(r,0)));
            int c=JOptionPane.showConfirmDialog(this,"Delete this student?","Confirm Delete",JOptionPane.OK_CANCEL_OPTION);
            if(c==JOptionPane.OK_OPTION){
                if(!UserRepo.deleteStudent(id)){ JOptionPane.showMessageDialog(this,"Delete failed."); return; }
                clearForm(fullName,studentIdNum,batch,major,username,password);
                refreshStudentTable(tm); refreshGradeCombos();
                JOptionPane.showMessageDialog(this,"Student deleted.");
            }
        });

        clear.addActionListener(e->{
            table.clearSelection();
            clearForm(fullName,studentIdNum,batch,major,username,password);
            dayBox.setSelectedItem(today.getDayOfMonth());
            monthBox.setSelectedIndex(today.getMonthValue()-1);
            yearBox.setSelectedItem(today.getYear());
        });

        GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(6,6,6,6); g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;
        int yrow=0;
        g.gridx=0; g.gridy=yrow; form.add(new JLabel("Full Name"), g);           g.gridx=1; form.add(fullName, g); yrow++;
        g.gridx=0; g.gridy=yrow; form.add(new JLabel("Student ID"), g);          g.gridx=1; form.add(studentIdNum, g); yrow++;
        g.gridx=0; g.gridy=yrow; form.add(new JLabel("Date of Birth (day month year)"), g);
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        dobPanel.setOpaque(false);
        dobPanel.add(dayBox); dobPanel.add(monthBox); dobPanel.add(yearBox);
        g.gridx=1; form.add(dobPanel, g); yrow++;
        g.gridx=0; g.gridy=yrow; form.add(new JLabel("Batch Number"), g);        g.gridx=1; form.add(batch, g); yrow++;
        g.gridx=0; g.gridy=yrow; form.add(new JLabel("Department"), g);          g.gridx=1; form.add(major, g); yrow++;
        g.gridx=0; g.gridy=yrow; form.add(new JLabel("Username"), g);            g.gridx=1; form.add(username, g); yrow++;
        g.gridx=0; g.gridy=yrow; form.add(new JLabel("Password (leave blank to keep existing on Update)"), g); g.gridx=1; form.add(password, g); yrow++;

        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        actions.setOpaque(false);
        actions.add(add); actions.add(update); actions.add(del); actions.add(clear);

        JPanel north=new JPanel(new BorderLayout(12,12));
        north.setOpaque(false);
        north.add(form,BorderLayout.CENTER); north.add(actions,BorderLayout.SOUTH);

        p.add(north,BorderLayout.NORTH);
        JScrollPane tableSp=new JScrollPane(table);
        Theme.stylePanel(tableSp);
        p.add(tableSp,BorderLayout.CENTER);

        refreshStudentTable(tm);
        return p;
    }

    private static String s(Object o){ return o==null? "": o.toString(); }
    private static void clearForm(JTextField fullName,JTextField studentIdNum,JTextField batch,JTextField major,JTextField username,JPasswordField password){
        fullName.setText(""); studentIdNum.setText(""); batch.setText(""); major.setText(""); username.setText(""); password.setText("");
    }
    private void refreshStudentTable(DefaultTableModel tm){
        tm.setRowCount(0);
        for(User u:UserRepo.students()){
            tm.addRow(new Object[]{
                u.id, u.fullName,
                u.studentNumber==null? "": u.studentNumber,
                u.dateOfBirth==null? "": u.dateOfBirth.toString(),
                u.batchNumber==null? "": u.batchNumber,
                u.major==null? "": u.major,
                u.username
            });
        }
    }

    private JPanel gradePanel() {
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        JComboBox<User> students=new JComboBox<>(gradeStudentsModel);
        students.setRenderer(new DefaultListCellRenderer(){ @Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){ super.getListCellRendererComponent(l,v,i,s,f); if(v instanceof User u) setText(u.toString()); return this; }});

        JComboBox<Course> courses=new JComboBox<>(gradeCoursesModel);
        courses.setRenderer(new DefaultListCellRenderer(){ @Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){ super.getListCellRendererComponent(l,v,i,s,f); if(v instanceof Course c) setText(c.code+" - "+c.title); return this; }});

        JTextField marksField = new JTextField();
        JComboBox<Double> creditsBox=new JComboBox<>(new Double[]{3.0,1.5});

        JLabel previewLetter = new JLabel("Letter: –");
        JLabel previewPoint  = new JLabel("Point: –");

        DocumentListener dl = new DocumentListener() {
            private void update() {
                String t = marksField.getText().trim();
                if (t.isEmpty()) { previewLetter.setText("Letter: –"); previewPoint.setText("Point: –"); return; }
                try {
                    int m = Integer.parseInt(t);
                    if (m < 0 || m > 100) { previewLetter.setText("Letter: –"); previewPoint.setText("Point: –"); return; }
                    previewLetter.setText("Letter: " + GradeRepo.letterFromMarks(m));
                    previewPoint.setText("Point: " + String.format("%.2f", GradeRepo.pointsFromMarks(m)));
                } catch (NumberFormatException ex) {
                    previewLetter.setText("Letter: –");
                    previewPoint.setText("Point: –");
                }
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        marksField.getDocument().addDocumentListener(dl);

        JButton assign=new JButton("Assign (Compute Grade)");
        Theme.styleButton(assign, Theme.PURPLE);

        String[] cols = {"Course", "Marks", "Credits", "Letter", "Point"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){ return false; } };
        JTable table = new JTable(tm);
        JScrollPane tableScroll = new JScrollPane(table);
        Theme.stylePanel(tableScroll);

        JLabel cgpaLabel = new JLabel("CGPA: –");
        JPanel cgpaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cgpaPanel.setOpaque(false);
        cgpaPanel.add(cgpaLabel);

        Runnable refreshStudentGradesAndCgpa = () -> {
            tm.setRowCount(0);
            User stu = (User)students.getSelectedItem();
            if (stu == null) { cgpaLabel.setText("CGPA: –"); return; }
            double sumWeighted = 0.0, sumCredits = 0.0;
            for (Grade g : GradeRepo.byStudent(stu.id)) {
                Course c = CourseRepo.findById(g.courseId);
                String cname = (c==null) ? ("Course "+g.courseId) : (c.code+" - "+c.title);
                tm.addRow(new Object[]{cname, g.marks, g.credits, g.letter, String.format("%.2f", g.points)});
                sumWeighted += g.points * g.credits;
                sumCredits  += g.credits;
            }
            if (sumCredits > 0) cgpaLabel.setText("CGPA: " + String.format("%.2f", sumWeighted/sumCredits));
            else cgpaLabel.setText("CGPA: –");
        };

        students.addActionListener(e -> refreshStudentGradesAndCgpa.run());

        assign.addActionListener(e->{
            User stu=(User)students.getSelectedItem(); Course c=(Course)courses.getSelectedItem();
            if(stu==null||c==null){ JOptionPane.showMessageDialog(this,"Select student and course"); return; }
            String t = marksField.getText().trim();
            if(t.isEmpty()){ JOptionPane.showMessageDialog(this,"Enter marks (0–100)"); return; }
            int m;
            try{ m=Integer.parseInt(t); }catch(NumberFormatException ex){ JOptionPane.showMessageDialog(this,"Marks must be a number between 0 and 100"); return; }
            if(m<0||m>100){ JOptionPane.showMessageDialog(this,"Marks must be between 0 and 100"); return; }
            Double cr=(Double)creditsBox.getSelectedItem(); if(cr==null) cr=3.0;
            GradeRepo.assignFromMarks(stu.id,c.id,m,cr);
            refreshStudentGradesAndCgpa.run();
            JOptionPane.showMessageDialog(this,"Saved: "+stu.fullName+" • "+c.code+" • "+GradeRepo.letterFromMarks(m)+" ("+String.format("%.2f",GradeRepo.pointsFromMarks(m))+")");
        });

        JPanel top=new JPanel(new GridBagLayout());
        Theme.stylePanel(top);
        GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(8,8,8,8); g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;

        int y=0;
        g.gridx=0; g.gridy=y; top.add(new JLabel("Student"), g); g.gridx=1; top.add(students, g); y++;
        g.gridx=0; g.gridy=y; top.add(new JLabel("Course"),  g); g.gridx=1; top.add(courses, g); y++;
        g.gridx=0; g.gridy=y; top.add(new JLabel("Marks (0–100)"), g); g.gridx=1; top.add(marksField, g); y++;
        g.gridx=0; g.gridy=y; top.add(new JLabel("Credits"), g); g.gridx=1; top.add(creditsBox, g); y++;

        JPanel preview = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        preview.setOpaque(false);
        preview.add(previewLetter); preview.add(previewPoint);
        g.gridx=1; g.gridy=y; top.add(preview, g); y++;

        g.gridx=1; g.gridy=y; top.add(assign, g);

        p.add(top,BorderLayout.NORTH);
        p.add(tableScroll,BorderLayout.CENTER);
        p.add(cgpaPanel, BorderLayout.SOUTH);

        refreshStudentGradesAndCgpa.run();
        return p;
    }

    private void refreshGradeCombos(){
        gradeStudentsModel.removeAllElements(); for(User u:UserRepo.students()) gradeStudentsModel.addElement(u);
        gradeCoursesModel.removeAllElements(); for(Course c:CourseRepo.list()) gradeCoursesModel.addElement(c);
    }

    private JPanel lostFoundPanel() {
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        JPanel top=new JPanel(new GridLayout(0,1,8,8));
        Theme.stylePanel(top);

        JTextField name=new JTextField(); JTextArea desc=new JTextArea(3,40);
        JButton report=new JButton("Report Lost Item");
        Theme.styleButton(report, Theme.WARNING);

        DefaultListModel<LostItem> model=new DefaultListModel<>();
        JList<LostItem> list=new JList<>(model);
        Theme.stylePanel(list);
        JButton claim=new JButton("Mark Selected as Claimed");
        Theme.styleButton(claim, Theme.SUCCESS);

        report.addActionListener(e->{ LostItemRepo.report(null, name.getText(), desc.getText()); name.setText(""); desc.setText(""); refreshLost(model); });
        claim.addActionListener(e->{
            LostItem li=list.getSelectedValue(); if(li==null){ JOptionPane.showMessageDialog(this,"Select an item"); return; }
            LostItemRepo.markClaimed(li.id); refreshLost(model);
        });

        top.add(new JLabel("Item name")); top.add(name);
        top.add(new JLabel("Description")); top.add(new JScrollPane(desc));
        top.add(report);

        p.add(top,BorderLayout.NORTH); p.add(new JScrollPane(list),BorderLayout.CENTER);
        JPanel south=new JPanel(new FlowLayout(FlowLayout.RIGHT)); south.setOpaque(false); south.add(claim);
        p.add(south,BorderLayout.SOUTH);
        refreshLost(model); return p;
    }
    private void refreshLost(DefaultListModel<LostItem> model){ model.clear(); for(LostItem li:LostItemRepo.list()) model.addElement(li); }

    private JPanel feedbackPanel() {
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);
        DefaultListModel<String> model=new DefaultListModel<>();
        JList<String> list=new JList<>(model);
        Theme.stylePanel(list);
        p.add(new JScrollPane(list),BorderLayout.CENTER);
        refreshFeedback(model);
        return p;
    }
    private void refreshFeedback(DefaultListModel<String> model){
        model.clear();
        for(Feedback f:FeedbackRepo.list()){
            String who="Anonymous";
            if(f.studentId!=null){
                User u=UserRepo.findById(f.studentId);
                who=(u!=null)? (u.fullName+" ("+u.username+")") : ("Student#"+f.studentId);
            }
            String to = (f.facultyName==null || f.facultyName.isEmpty()) ? "" : (" | To: " + f.facultyName);
            model.addElement(f.createdAt+" | "+who+to+" — "+f.message);
        }
    }
}

class StudentDashboard extends JFrame {
    private final User student;
    private static final String DEFAULT_ROUTINE_NAME = "Class_Routine_CSE(DAY)_Summer2025_EF-07-05-25 (1).pdf";

    public StudentDashboard(User student){
        this.student=student;
        setTitle("Student Dashboard - "+student.fullName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100,760);
        setLocationRelativeTo(null);
        Theme.decorateFrame(this);
        setLayout(new BorderLayout());

        JTabbedPane tabs=new JTabbedPane();
        tabs.setBackground(Theme.SURFACE);
        tabs.addTab("Enrolled Courses", Theme.tabIcon("En", Theme.SUCCESS), coursePanel());
        tabs.addTab("Schedule", Theme.tabIcon("Sc", Theme.ACCENT), schedulePanel());
        tabs.addTab("Grades", Theme.tabIcon("Gr", Theme.PURPLE), gradesPanel());
        tabs.addTab("Announcements", Theme.tabIcon("An", Theme.PINK), announcementsPanel());
        tabs.addTab("Feedback", Theme.tabIcon("Fb", Theme.TEAL), feedbackPanel());
        tabs.addTab("Lost & Found", Theme.tabIcon("LF", Theme.WARNING), lostFoundPanel());
        add(tabs,BorderLayout.CENTER);

        JButton logout=new JButton("Logout");
        Theme.styleButton(logout, Theme.DANGER);
        logout.addActionListener(e->{
            int c=JOptionPane.showConfirmDialog(this,"Are you sure you want to log out?","Confirm Logout",JOptionPane.YES_NO_OPTION);
            if(c==JOptionPane.YES_OPTION){
                dispose();
                new LoginFrame(user->{
                    if(user.role==Role.FACULTY) new FacultyDashboard(user).setVisible(true);
                    else new StudentDashboard(user).setVisible(true);
                }).setVisible(true);
            }
        });
        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.LEFT)); bottom.setBackground(Theme.BG); bottom.add(logout); add(bottom,BorderLayout.SOUTH);
    }

    private JPanel coursePanel(){
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        DefaultListModel<Course> model=new DefaultListModel<>();
        JList<Course> courses=new JList<>(model);
        courses.setEnabled(false);
        Theme.stylePanel(courses);

        JScrollPane sp = new JScrollPane(courses);
        sp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.PRIMARY.darker(),1,false),"Enrolled Courses"));
        p.add(sp,BorderLayout.CENTER);

        refreshCourses(model);
        return p;
    }
    private void refreshCourses(DefaultListModel<Course> model){
        model.clear();
        for(Course c:CourseRepo.list()) model.addElement(c);
    }

    private JPanel schedulePanel(){
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        JPanel routine = new JPanel(new GridBagLayout());
        Theme.stylePanel(routine);
        routine.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.ACCENT.darker(),1,false),"Class Routine (PDF)"));
        GridBagConstraints rg = new GridBagConstraints();
        rg.insets = new Insets(8,8,8,8);
        rg.fill = GridBagConstraints.HORIZONTAL;
        rg.weightx = 1.0;

        JLabel msg = new JLabel("Download your Class Routine CSE (DAY) Summer 2025 from here:");
        JButton openBtn  = new JButton("Open");
        JButton printBtn = new JButton("Print");
        Theme.styleButton(openBtn, Theme.TEAL);
        Theme.styleButton(printBtn, Theme.ACCENT);

        rg.gridx=0; rg.gridy=0; rg.gridwidth=2; routine.add(msg, rg);
        rg.gridwidth=1; rg.gridy=1; rg.gridx=0; routine.add(openBtn, rg);
        rg.gridx=1; routine.add(printBtn, rg);

        openBtn.addActionListener(e -> {
            File f = findRoutinePdf(p);
            if (f == null) return;
            try { Desktop.getDesktop().open(f); }
            catch (Exception ex) { JOptionPane.showMessageDialog(p, "Cannot open PDF: " + ex.getMessage()); }
        });
        printBtn.addActionListener(e -> {
            File f = findRoutinePdf(p);
            if (f == null) return;
            try { Desktop.getDesktop().print(f); }
            catch (Exception ex) { JOptionPane.showMessageDialog(p, "Cannot print PDF: " + ex.getMessage()); }
        });

        p.add(routine, BorderLayout.NORTH);
        return p;
    }

    private File findRoutinePdf(Component parent){
        java.util.List<File> candidates = new ArrayList<>();
        candidates.add(new File(DEFAULT_ROUTINE_NAME));
        String home = System.getProperty("user.home");
        if (home != null) {
            candidates.add(new File(home, "Downloads/" + DEFAULT_ROUTINE_NAME));
            candidates.add(new File(home, "Desktop/" + DEFAULT_ROUTINE_NAME));
        }
        for (File f : candidates) if (f.isFile()) return f;

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Locate Class Routine PDF");
        fc.setSelectedFile(new File(DEFAULT_ROUTINE_NAME));
        int res = fc.showOpenDialog(parent);
        if (res == JFileChooser.APPROVE_OPTION) return fc.getSelectedFile();
        return null;
    }

    private JPanel gradesPanel(){
        JPanel p = new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        String[] cols = {"Course", "Marks", "Credits", "Letter", "Point"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable table = new JTable(tm);
        table.setFillsViewportHeight(true);
        JScrollPane tableSp=new JScrollPane(table);
        Theme.stylePanel(tableSp);

        JButton preview = new JButton("Print Marksheet");
        Theme.styleButton(preview, Theme.PRIMARY);
        JLabel  cgpaLbl = new JLabel("CGPA: –");

        preview.addActionListener(e -> showMarksheetPreview(student));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.setOpaque(false);
        top.add(preview);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        bottom.add(cgpaLbl);

        p.add(top, BorderLayout.NORTH);
        p.add(tableSp, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        fillGradesTable(tm);
        cgpaLbl.setText("CGPA: " + computeCgpaString(student.id));
        return p;
    }

    private void fillGradesTable(DefaultTableModel tm){
        tm.setRowCount(0);
        for(Grade g:GradeRepo.byStudent(student.id)){
            Course c=CourseRepo.findById(g.courseId);
            String title=c!=null? (c.code+" - "+c.title) : ("Course "+g.courseId);
            tm.addRow(new Object[]{
                title,
                g.marks,
                g.credits,
                g.letter,
                String.format("%.2f", g.points)
            });
        }
    }

    private String computeCgpaString(int studentId){
        double w=0, csum=0;
        for(Grade g:GradeRepo.byStudent(studentId)){ w+=g.points*g.credits; csum+=g.credits; }
        return csum>0? String.format("%.2f", w/csum) : "–";
    }

    private String buildMarksheetText(User stu){
        StringBuilder sb = new StringBuilder();
        String date = Clock.now();
        sb.append("METROPOLITAN UNIVERSITY BANGLADESH\n");
        sb.append("STUDENT MARKSHEET\n");
        sb.append("Generated: ").append(date).append("\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("Name      : ").append(stu.fullName).append("\n");
        if(stu.studentNumber!=null) sb.append("Student ID: ").append(stu.studentNumber).append("\n");
        if(stu.batchNumber!=null)   sb.append("Batch     : ").append(stu.batchNumber).append("\n");
        if(stu.major!=null)         sb.append("Department: ").append(stu.major).append("\n");
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("%-10s  %-28s  %5s  %6s  %5s\n", "Code", "Course", "Marks", "Credit", "Point"));
        sb.append("------------------------------------------------------------\n");
        double w=0, csum=0;
        for(Grade g:GradeRepo.byStudent(stu.id)){
            Course c = CourseRepo.findById(g.courseId);
            String code = (c==null? ("C"+g.courseId) : c.code);
            String title = (c==null? ("Course "+g.courseId) : c.title);
            sb.append(String.format("%-10s  %-28s  %5d  %6s  %5.2f\n",
                    code, title.length()>28? title.substring(0,28) : title, g.marks,
                    (Math.abs(g.credits-Math.rint(g.credits))<1e-9? String.valueOf((int)Math.rint(g.credits)) : String.valueOf(g.credits)),
                    g.points));
            w += g.points*g.credits; csum+=g.credits;
        }
        sb.append("------------------------------------------------------------\n");
        sb.append("TOTAL CGPA: ").append(csum>0? String.format("%.2f", w/csum) : "–").append("\n");
        sb.append("------------------------------------------------------------\n");
        return sb.toString();
    }

    private void showMarksheetPreview(User stu){
        JDialog dlg = new JDialog(this, "Marksheet Preview", true);
        dlg.setSize(820, 600);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8,8));

        JTextArea ta = new JTextArea(buildMarksheetText(stu));
        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        dlg.add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton printBtn = new JButton("Print");
        JButton backBtn  = new JButton("Back");
        Theme.styleButton(printBtn, Theme.PRIMARY);
        Theme.styleButton(backBtn, Theme.TEAL);

        printBtn.addActionListener(ev -> {
            try {
                boolean ok = ta.print();
                if (!ok) JOptionPane.showMessageDialog(dlg, "Print was cancelled.");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(dlg, "Printing failed: " + ex.getMessage());
            }
        });
        backBtn.addActionListener(ev -> dlg.dispose());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(printBtn);
        btns.add(backBtn);
        dlg.add(btns, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private JPanel announcementsPanel(){
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);
        DefaultListModel<Announcement> model=new DefaultListModel<>();
        JList<Announcement> list=new JList<>(model);
        Theme.stylePanel(list);
        JButton refresh=new JButton("Refresh"); Theme.styleButton(refresh, Theme.ACCENT);
        refresh.addActionListener(e->refreshAnnouncements(model));
        JPanel top=new JPanel(new FlowLayout(FlowLayout.RIGHT)); top.setOpaque(false); top.add(refresh);
        p.add(top,BorderLayout.NORTH); p.add(new JScrollPane(list),BorderLayout.CENTER);
        refreshAnnouncements(model); return p;
    }
    private void refreshAnnouncements(DefaultListModel<Announcement> m){ m.clear(); for(Announcement a:AnnouncementRepo.list()) m.addElement(a); }

    private JPanel feedbackPanel(){
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        JPanel form = new JPanel(new GridBagLayout());
        Theme.stylePanel(form);

        JTextField facultyName = new JTextField();
        JTextArea msg=new JTextArea(5,40);
        JCheckBox anonymous=new JCheckBox("Submit anonymously");
        JButton send=new JButton("Send");
        Theme.styleButton(send, Theme.SUCCESS);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        int y=0;
        g.gridx=0; g.gridy=y; g.weightx=0; form.add(new JLabel("Faculty Name (optional)"), g);
        g.gridx=1; g.gridy=y; g.weightx=1; form.add(facultyName, g); y++;

        g.gridx=0; g.gridy=y; g.weightx=0; form.add(new JLabel("Message / Description"), g);
        g.gridx=1; g.gridy=y; g.weightx=1;
        JScrollPane msgScroll = new JScrollPane(msg);
        msgScroll.setPreferredSize(new Dimension(0, 140));
        form.add(msgScroll, g); y++;

        g.gridx=1; g.gridy=y; g.weightx=1; form.add(anonymous, g); y++;

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setOpaque(false);
        south.add(send);

        send.addActionListener(e->{
            String m=msg.getText();
            String f=facultyName.getText();
            if(m==null||m.trim().isEmpty()){ JOptionPane.showMessageDialog(this,"Message is empty"); return; }
            FeedbackRepo.submit(anonymous.isSelected()? null : student.id, f, m);
            JOptionPane.showMessageDialog(this,"Thanks for your feedback!");
            facultyName.setText("");
            msg.setText("");
            anonymous.setSelected(false);
        });

        p.add(form,BorderLayout.CENTER);
        p.add(south,BorderLayout.SOUTH);
        return p;
    }

    private JPanel lostFoundPanel(){
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBackground(Theme.BG);

        JPanel top=new JPanel(new GridLayout(0,1,8,8));
        Theme.stylePanel(top);

        JTextField name=new JTextField(); JTextArea desc=new JTextArea(3,40);
        JButton report=new JButton("Report Lost Item");
        Theme.styleButton(report, Theme.WARNING);

        DefaultListModel<LostItem> model=new DefaultListModel<>();
        JList<LostItem> list=new JList<>(model); list.setEnabled(false);
        Theme.stylePanel(list);
        JButton refresh=new JButton("Refresh"); Theme.styleButton(refresh, Theme.ACCENT);

        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT)); right.setOpaque(false); right.add(refresh);

        top.add(new JLabel("Item name")); top.add(name);
        top.add(new JLabel("Description")); top.add(new JScrollPane(desc));
        top.add(report);
        p.add(top,BorderLayout.NORTH);
        p.add(right,BorderLayout.SOUTH); p.add(new JScrollPane(list),BorderLayout.CENTER);

        report.addActionListener(e->{
            LostItemRepo.report(student.id, name.getText(), desc.getText());
            JOptionPane.showMessageDialog(this,"Reported!");
            name.setText(""); desc.setText(""); refreshLost(model);
        });
        refresh.addActionListener(e->refreshLost(model));

        refreshLost(model); return p;
    }
    private void refreshLost(DefaultListModel<LostItem> m){ m.clear(); for(LostItem li:LostItemRepo.list()) m.addElement(li); }
}




