package org.imageconverter.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/gui/images")
class ImageConverterGuiController {
    
//    private final BookService bookService;
//
//    public BookController(BookService bookService) {
//	this.bookService = bookService;
//    }
//
//    @GetMapping("/books.html")
//    public String all(final Model model) {
//	model.addAttribute("books", bookService.findAll());
//	return "books/list";
//    }
//
//    @GetMapping(value = "/books.html", params = "isbn")
//    public String get(@RequestParam("isbn") final String isbn, final Model model) {
//
//	bookService.find(isbn).ifPresent(book -> model.addAttribute("book", book));
//
//	return "books/details";
//    }
//
//    @PostMapping("/books")
//    public Book create(@ModelAttribute final Book book) {
//	return bookService.create(book);
//    }
//
//    @GetMapping("/books/404")
//    public ResponseEntity<?> notFound() {
//	return ResponseEntity.notFound().build();
//    }
//
//    @GetMapping("/books/400")
//    public ResponseEntity<?> foo() {
//	return ResponseEntity.badRequest().build();
//    }
//
//    @GetMapping("/books/500")
//    public void error() {
//	throw new NullPointerException("Dummy NullPointerException.");
//    }

}
