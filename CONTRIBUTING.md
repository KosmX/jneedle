# How can I help?

## Requesting feature or request adding malware signature to the database

In this case, feel free to open an issue or contact me on [Minecraft Malware Prevention Alliance](https://mmpa.info/) Discord server.  
**Never upload malware to GitHub or Discord!** If you have a malware sample that you want to share with se, please share it via [Wormhole](https://wormhole.app/).  
<!-- or use malware store TODO -->

## Contribution
If you're familiar with [Kotlin](https://kotlinlang.org/) and you want to help with the project, feel free to open pull requests.  

### Pull-requests  
Please keep pull requests clean: only add one feature per pull request. Pull requests modifying the whole project and adding multiple unrelated features will be rejected.  
If you start working on a feature, please open a draft PR as soon as you can, and track your progress there.  
This way people will know that someone is already working on XXX.  


GitHub support checkboxes, you may use those:  

---
```md
- [x] ticked checkbox
- [ ] todo checkbox
```
- [x] ticked checkbox
- [ ] todo checkbox


---

## Coding
Follow common sense!

### No premature optimization
First, make a feature work, then you might profile and optimize it. (you may choose efficient algorithms)  

You may use Object Oriented or Functional style, but please avoid unnecessary abstraction.  
A readable code is better than an optimized or obfuscated code.  

### Readable code
Please avoid global variables and functions. Only use global if it is widely used in the project.
Group simple functions into Objects, or use extension functions.  

Multiple classes may be declared in a single file as long as those are closely related.  

If possible, avoid using reflection.  

Avoid [platform types](https://kotlinlang.org/docs/java-interop.html#null-safety-and-platform-types): If java library has no nullability annotation choose the expected type.
```kt
val mayBeNull = JavaClass.function() //  Any! NOT OK
val mayBeNull: Any = JavaClass.function() //  Any OK
```
