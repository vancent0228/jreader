package reader;

import pojo.BookVO;
import pojo.ChapterVO;
import site.BookSiteEnum;

import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

/**
 * 命令行阅读器
 */
public class ConsoleReader {


    private BookVO bookVO = null;

    private Scanner scanner;

    public ConsoleReader() {
    }

    public ConsoleReader(String[] args) {
        try {
            this.bookVO = BookSiteEnum.values()[Integer.parseInt(args[0])].getBookSite().search(args[1]).get(0);
            if (args.length == 3) {
                jump(Integer.parseInt(args[2]));
            }
        } catch (Exception e) {
        }
    }

    private void showContent() {
        for (int i = 0; i < bookVO.getContents().getChapters().size() - bookVO.getChapterIndex() && i < 20; i++) {
            ChapterVO chapterVO = bookVO.getContents().getChapters().get(bookVO.getChapterIndex() + i);
            sysPrintln(bookVO.getChapterIndex() + i + ":" + chapterVO.getChapterName() + (i == 0 ? "(*)" : ""));
        }
        String value = getInput("想看哪一个章节(n:显示下面20章)");
        if (value.equals("n")) {
            bookVO.setChapterIndex(bookVO.getChapterIndex() + 20);
            showContent();
        } else {
            while (true) {
                try {
                    int page = Integer.parseInt(value);
                    jump(page);
                    return;
                } catch (Exception e) {
                    value = getInput("请输入正确的章节序号");
                }
            }
        }
    }

    private void jump(int page) {
        bookVO.setChapterIndex(page);
    }

    private BookSiteEnum selectBookSite() {
        BookSiteEnum bookSiteEnum = null;

        while (bookSiteEnum == null) {
            try {
                for (int i = 0; i < BookSiteEnum.values().length; i++) {
                    sysPrintln((i + 1) + ":" + BookSiteEnum.values()[i].getDesc());
                }
                String siteNum = getInput("选择小说源");
                bookSiteEnum = BookSiteEnum.values()[Integer.parseInt(siteNum) - 1];
            } catch (Exception e) {
                System.err.println("小说源序号有误");
            }
        }
        return bookSiteEnum;
    }

    private BookVO selectBook(List<BookVO> list) {
        BookVO bookVO = null;
        while (bookVO == null) {
            try {
                for (int i = 1; i < list.size() + 1; i++) {
                    sysPrintln(i + ":" + list.get(i - 1).getBookName());
                }
                String value = getInput("想看哪本书");
                bookVO = list.get(Integer.parseInt(value) - 1);
            } catch (Exception e) {
                System.err.println("书本序号有误");
            }


        }
        return bookVO;
    }

    private String getInput(String desc) {
        if (desc != null && !desc.isEmpty()) {
            sysPrint(desc + ":");
        }
        String value = scanner.nextLine();
        if ("quit".equalsIgnoreCase(value)) {
            System.exit(0);
        }
        return value;
    }

    private void searchBook() {
        while (this.bookVO == null) {
            try {
                BookSiteEnum bookSiteEnum = selectBookSite();
                String value = getInput(bookSiteEnum.getDesc() + "   搜索");
                List<BookVO> list = bookSiteEnum.getBookSite().search(value);
                this.bookVO = selectBook(list);
                bookVO.cache();
                showContent();
            } catch (Exception e) {
                System.err.println("系统错误!");
            }

        }
    }

    public void start() {
        scanner = new Scanner(System.in);
        String value = null;
        do {
            searchBook();
            print();
            value = getInput("");
            if (value == null || value.trim().isEmpty()) {
                value = "n";
            }
            if (value.equalsIgnoreCase("n")) {
                jump(this.bookVO.getChapterIndex() + 1);
            } else if (value.equalsIgnoreCase("p")) {
                jump(this.bookVO.getChapterIndex() - 1);
            } else if (value.equalsIgnoreCase("m")) {
                showContent();
            } else if (value.equalsIgnoreCase("r")) {
                this.bookVO = null;
            } else if (value.startsWith("M:")) {
                String[] page = value.split(":", 2);
                if (page.length == 2) {
                    try {
                        int v = Integer.parseInt(page[1]);
                        jump(v);
                    } catch (Exception e) {

                    }
                }
            } else {
                jump(this.bookVO.getChapterIndex() + 1);
            }
        } while (!"quit".equalsIgnoreCase(value));

    }

    /**
     * 替换sysPrint
     */
    private void sysPrintln(String content) {
//        System.out.println(new String(content.getBytes(), StandardCharsets.UTF_8));
        System.out.println(content);
    }

    private void sysPrint(String content) {
//        System.out.print(new String(content.getBytes(), StandardCharsets.UTF_8));
        System.out.print(content);
    }


    private void print() {
        ChapterVO chapterVO = bookVO.getCurrentChapter();
        sysPrintln(chapterVO.getChapterName());
        sysPrintln(chapterVO.getContent());
        sysPrint(bookVO.getChapterIndex() + " >>> ");
        sysPrint(LocalTime.now().toString());
        sysPrint(" >>> n - 下一章 p - 前一章 m - 显示目录  M:[数字] - 跳转到相应章节 r - 重新看书 quit - 退出");
    }


    public static void main(String[] args) {
        ConsoleReader consoleReader = new ConsoleReader(args);
        consoleReader.start();

    }


}
