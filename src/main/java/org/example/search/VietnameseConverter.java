package org.example.search;

public class VietnameseConverter {
    public VietnameseConverter() {
    }

    public static String toTextNotMarked(String text) {
        if (text == null) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < text.length(); ++i) {
                char c = text.charAt(i);
                switch(c) {
                    case 'À':
                    case 'Á':
                    case 'Â':
                    case 'Ã':
                    case 'Ă':
                    case 'Ạ':
                    case 'Ả':
                    case 'Ấ':
                    case 'Ầ':
                    case 'Ẩ':
                    case 'Ẫ':
                    case 'Ậ':
                    case 'Ắ':
                    case 'Ằ':
                    case 'Ẳ':
                    case 'Ẵ':
                    case 'Ặ':
                        builder.append('A');
                        break;
                    case 'È':
                    case 'É':
                    case 'Ê':
                    case 'Ẹ':
                    case 'Ẻ':
                    case 'Ẽ':
                    case 'Ế':
                    case 'Ề':
                    case 'Ể':
                    case 'Ễ':
                    case 'Ệ':
                        builder.append('E');
                        break;
                    case 'Ì':
                    case 'Í':
                    case 'Ĩ':
                    case 'Ỉ':
                    case 'Ị':
                        builder.append('I');
                        break;
                    case 'Ò':
                    case 'Ó':
                    case 'Ô':
                    case 'Õ':
                    case 'Ơ':
                    case 'Ọ':
                    case 'Ỏ':
                    case 'Ố':
                    case 'Ồ':
                    case 'Ổ':
                    case 'Ỗ':
                    case 'Ộ':
                    case 'Ớ':
                    case 'Ờ':
                    case 'Ở':
                    case 'Ỡ':
                    case 'Ợ':
                        builder.append('O');
                        break;
                    case 'Ù':
                    case 'Ú':
                    case 'Ũ':
                    case 'Ư':
                    case 'Ụ':
                    case 'Ủ':
                    case 'Ứ':
                    case 'Ừ':
                    case 'Ử':
                    case 'Ữ':
                    case 'Ự':
                        builder.append('U');
                        break;
                    case 'Ý':
                    case 'Ỳ':
                    case 'Ỵ':
                    case 'Ỷ':
                    case 'Ỹ':
                        builder.append('Y');
                        break;
                    case 'à':
                    case 'á':
                    case 'â':
                    case 'ã':
                    case 'ă':
                    case 'ạ':
                    case 'ả':
                    case 'ấ':
                    case 'ầ':
                    case 'ẩ':
                    case 'ẫ':
                    case 'ậ':
                    case 'ắ':
                    case 'ằ':
                    case 'ẳ':
                    case 'ẵ':
                    case 'ặ':
                        builder.append('a');
                        break;
                    case 'è':
                    case 'é':
                    case 'ê':
                    case 'ẹ':
                    case 'ẻ':
                    case 'ẽ':
                    case 'ế':
                    case 'ề':
                    case 'ể':
                    case 'ễ':
                    case 'ệ':
                        builder.append('e');
                        break;
                    case 'ì':
                    case 'í':
                    case 'ĩ':
                    case 'ỉ':
                    case 'ị':
                        builder.append('i');
                        break;
                    case 'ò':
                    case 'ó':
                    case 'ô':
                    case 'õ':
                    case 'ơ':
                    case 'ọ':
                    case 'ỏ':
                    case 'ố':
                    case 'ồ':
                    case 'ổ':
                    case 'ỗ':
                    case 'ộ':
                    case 'ớ':
                    case 'ờ':
                    case 'ở':
                    case 'ỡ':
                    case 'ợ':
                        builder.append('o');
                        break;
                    case 'ù':
                    case 'ú':
                    case 'ũ':
                    case 'ư':
                    case 'ụ':
                    case 'ủ':
                    case 'ứ':
                    case 'ừ':
                    case 'ử':
                    case 'ữ':
                    case 'ự':
                        builder.append('u');
                        break;
                    case 'ý':
                    case 'ỳ':
                    case 'ỵ':
                    case 'ỷ':
                    case 'ỹ':
                        builder.append('y');
                        break;
                    case 'Đ':
                        builder.append('D');
                        break;
                    case 'đ':
                        builder.append('d');
                        break;
                    default:
                        builder.append(c);
                }
            }

            return builder.toString();
        }
    }

    public static String toAlias(String text) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            switch(c) {
                case 'À':
                case 'Á':
                case 'Â':
                case 'Ã':
                case 'Ă':
                case 'Ạ':
                case 'Ả':
                case 'Ấ':
                case 'Ầ':
                case 'Ẩ':
                case 'Ẫ':
                case 'Ậ':
                case 'Ắ':
                case 'Ằ':
                case 'Ẳ':
                case 'Ẵ':
                case 'Ặ':
                    builder.append('A');
                    break;
                case 'È':
                case 'É':
                case 'Ê':
                case 'Ẹ':
                case 'Ẻ':
                case 'Ẽ':
                case 'Ế':
                case 'Ề':
                case 'Ể':
                case 'Ễ':
                case 'Ệ':
                    builder.append('E');
                    break;
                case 'Ì':
                case 'Í':
                case 'Ĩ':
                case 'Ỉ':
                case 'Ị':
                    builder.append('I');
                    break;
                case 'Ò':
                case 'Ó':
                case 'Ô':
                case 'Õ':
                case 'Ơ':
                case 'Ọ':
                case 'Ỏ':
                case 'Ố':
                case 'Ồ':
                case 'Ổ':
                case 'Ỗ':
                case 'Ộ':
                case 'Ớ':
                case 'Ờ':
                case 'Ở':
                case 'Ỡ':
                case 'Ợ':
                    builder.append('O');
                    break;
                case 'Ù':
                case 'Ú':
                case 'Ũ':
                case 'Ư':
                case 'Ụ':
                case 'Ủ':
                case 'Ứ':
                case 'Ừ':
                case 'Ử':
                case 'Ữ':
                case 'Ự':
                    builder.append('U');
                    break;
                case 'Ý':
                case 'Ỳ':
                case 'Ỵ':
                case 'Ỷ':
                case 'Ỹ':
                    builder.append('Y');
                    break;
                case 'à':
                case 'á':
                case 'â':
                case 'ã':
                case 'ă':
                case 'ạ':
                case 'ả':
                case 'ấ':
                case 'ầ':
                case 'ẩ':
                case 'ẫ':
                case 'ậ':
                case 'ắ':
                case 'ằ':
                case 'ẳ':
                case 'ẵ':
                case 'ặ':
                    builder.append('a');
                    break;
                case 'è':
                case 'é':
                case 'ê':
                case 'ẹ':
                case 'ẻ':
                case 'ẽ':
                case 'ế':
                case 'ề':
                case 'ể':
                case 'ễ':
                case 'ệ':
                    builder.append('e');
                    break;
                case 'ì':
                case 'í':
                case 'ĩ':
                case 'ỉ':
                case 'ị':
                    builder.append('i');
                    break;
                case 'ò':
                case 'ó':
                case 'ô':
                case 'õ':
                case 'ơ':
                case 'ọ':
                case 'ỏ':
                case 'ố':
                case 'ồ':
                case 'ổ':
                case 'ỗ':
                case 'ộ':
                case 'ớ':
                case 'ờ':
                case 'ở':
                case 'ỡ':
                case 'ợ':
                    builder.append('o');
                    break;
                case 'ù':
                case 'ú':
                case 'ũ':
                case 'ư':
                case 'ụ':
                case 'ủ':
                case 'ứ':
                case 'ừ':
                case 'ử':
                case 'ữ':
                case 'ự':
                    builder.append('u');
                    break;
                case 'ý':
                case 'ỳ':
                case 'ỵ':
                case 'ỷ':
                case 'ỹ':
                    builder.append('y');
                    break;
                case 'Đ':
                    builder.append('D');
                    break;
                case 'đ':
                    builder.append('d');
                    break;
                default:
                    if (Character.isLetterOrDigit(c)) {
                        builder.append(c);
                    } else if (builder.length() > 0 && i < text.length() - 1) {
                        char last = builder.charAt(builder.length() - 1);
                        if (last != '-') {
                            builder.append('-');
                        }
                    }
            }
        }

        while(builder.length() > 0 && builder.charAt(builder.length() - 1) == '-') {
            builder.delete(builder.length() - 1, builder.length());
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(toAlias("?.---))__+ Giám đốc CIA: Bắc Hàn 'ở ngưỡng' năng lực hạt nhân ? --??? .. -"));
    }
}
