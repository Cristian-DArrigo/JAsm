public class Stack {
    public static class Node {
        private String etiquette;
        private int lineNumber; // used to store etiquette:lineNumber pairs
        private Node next;

        public Node(String etiquette, int lineNumber) {
            this.setEtiquette(etiquette);
            this.setLineNumber(lineNumber);
            this.setNext(null);
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public String getEtiquette() {
            return etiquette;
        }

        public void setEtiquette(String etiquette) {
            this.etiquette = etiquette;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }
    }

    private Node head;

    public Stack() {
        head = null;
    }

    public void push(String etiquette, int lineNumber) {
        Node node = new Node(etiquette, lineNumber);
        this.push(node);
    }

    public void push(Node data) {
        if (head != null) {
            data.setNext(head);
        }
        head = data;
    }

    public Node pop() {
        if (head == null) {
            return null;
        }
        Node node = head;
        head = head.getNext();
        return node;
    }

    public Node peek() {
        return head;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void show() {
        // show stack in a table like format
        if (isEmpty()) {
            System.out.println("\t\t\t\tStack is empty.");
            return;
        }
        Node node = head;
        while (node!= null) {
            System.out.println("\t\t\t\t" + node.getEtiquette() + ": " + node.getLineNumber());
            node = node.getNext();
        }

    }
}
