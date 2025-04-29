import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class RecursiveLister extends JFrame {

    private final JTextArea textArea   = new JTextArea(25, 60);
    private final JButton   startBtn   = new JButton("Start");
    private final JButton   quitBtn    = new JButton("Quit");

    public RecursiveLister() {
        super("Recursive File Lister");

        JLabel title = new JLabel("Recursive File Lister (Lab 11)", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.add(startBtn);
        south.add(quitBtn);
        add(south, BorderLayout.SOUTH);

        startBtn.addActionListener(this::handleStart);
        quitBtn.addActionListener(e -> System.exit(0));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleStart(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path dir = chooser.getSelectedFile().toPath();
            textArea.setText("");          // clear previous run
            startBtn.setEnabled(false);    // lock UI while working
            new ListWorker(dir).execute(); // run in background
        }
    }

    private class ListWorker extends SwingWorker<Void, String> {
        private final Path startDir;
        ListWorker(Path startDir) { this.startDir = startDir; }

        @Override protected Void doInBackground() throws Exception {
            walk(startDir);
            return null;
        }

        private void walk(Path dir) throws IOException {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
                for (Path p : ds) {
                    publish(p.toString());
                    if (Files.isDirectory(p)) {
                        walk(p);
                    }
                }
            }
        }

        @Override protected void process(List<String> chunks) {
            for (String s : chunks) {
                textArea.append(s + System.lineSeparator());
            }
        }

        @Override protected void done() {
            startBtn.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RecursiveLister::new);
    }
}
