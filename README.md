PRs are welcome, will gladly help with any questions you may have, but pls try to avoid unnecessary refactoring and features that may cause considerable performance degradation, as such PRs won't be accepted. If in doubt, create an issue to discuss whether feature will be acceptable before starting investing into the solution.

If you need help with building the project or understanding the code, feel free to drop at https://t.me/klesun_productions, we can chat there ;)

____________________________

Autocomplete keys of associative arrays defined in other functions.

Jetbrains Repository: [9927-deep-assoc-completion](https://plugins.jetbrains.com/plugin/9927-deep-assoc-completion)

![](https://user-images.githubusercontent.com/5202330/41823825-f82a5724-780e-11e8-9a8e-4eb37c89aa53.png)


## Features description

(a bit more relaxed usage guide can be found [here](https://github.com/klesun/phpstorm-deep-keys/blob/master/docs/deep-keys-overview.md))

- ### Completion from expression
    When you are going to type an associative key of a variable, like in `$user['']`, put caret between quotes and press `ctrl` + `space`. The plugin will analyze your code, determine what keys does `$user` have and suggest completion.

- ### Go To Definition
    ![Go To Definition](https://cloud.githubusercontent.com/assets/5202330/26428215/284b1988-40e9-11e7-9a44-746145c5393f.png)
    
    To go to the key definition, hover on it and press `ctrl` + `click` or put carret on it and press `ctrl` + `b`.

- ### Completion from phpdoc

    [See formats description](https://github.com/klesun/deep-assoc-completion/issues/63)

    ![Completion from phpdoc](https://cloud.githubusercontent.com/assets/5202330/26426602/0f72f554-40e2-11e7-8873-30b873310746.png)

    You can specify function argument type using `@param SomeType $varName = Some::phpExpression()`, like `@param $anime = ['genre' => 'shounen', 'studio' => 'Shaft']`. `=` is mandatory and expression must be a valid php expression. Class methods can be specified either with complete namespace like `\Very\Long\Namespace\ClassName::funcName()`, or with just `ClassName::funcName()`.

    You can specify `@return` array keys as well:
    ```cpp
    /**
     * @return array [
     *     'success' => true,
     *     'formObject' => new Form,
     *     'errors' => [],
     * ]
     */
    public static function processForm($arr);
    ```
    ![Completion from @return phpdoc](https://i.stack.imgur.com/vgZM9.png)

- ### Object type info in an associative array
    ![Object type info in an associative array](https://user-images.githubusercontent.com/5202330/30355696-9d6aa368-983d-11e7-8b8a-6b4f5afcee0e.png)
    
    Phpstorm does not give you method name completion when object is located in an associative array? Don't be sad, this plugin is exactly what you need!
    
- ### To N-th Test
    ![To N-th Test](https://user-images.githubusercontent.com/5202330/48870020-e6310280-ede7-11e8-9a70-33b64fdcc574.png)
    
    Did you ever want an ability to find out which exactly test case does phpunit mean by the `with data set "17"` without manually counting them? You can find this feature in _Tools -> deep-assoc-completion -> To N-th Test_. It moves your caret to the test case with the order you specify in the popup. If there are multiple `@dataProvider` functions in the file, the function caret is currently in will be used. This action will work correctly only on more or less straightforward `@dataProvider`-s.  
    
- ### String value completion
    ![String Value Completion](https://user-images.githubusercontent.com/5202330/48870527-e205e480-ede9-11e8-824c-750088b76fa4.png)
      
    ![String Value Completion](https://user-images.githubusercontent.com/5202330/48870610-2b563400-edea-11e8-93c1-c8bbd973726b.png)  
    
- ### Transpile to JS code (not related to completion anyhow, but whatever)
    
    ![Transpile to JS code](https://user-images.githubusercontent.com/5202330/51703012-293c6b80-200d-11e9-9479-e51c5f7bbfaf.png)  
    
## Completion sources
    
- ### Argument type resolution based on what was passed to the function
    ![Keep Track of What is Passed to the Function](https://user-images.githubusercontent.com/5202330/48870882-280f7800-edeb-11e8-9a72-fe66b1af1fd5.png)
    
    ![Infer type based on function usage when inside function](https://user-images.githubusercontent.com/5202330/48870975-88061e80-edeb-11e8-9501-c525a2a92e6a.png)
    
    Extremely useful for small private helper functions - you don't need to document args in each of them to get completion.
    
- ### Keys from PDOStatement::fetch() if your Database is connected to IDEA
    ![PDO completion](https://user-images.githubusercontent.com/5202330/34743879-3e690ff0-f583-11e7-8dee-dd8c86b78917.png)
    
- ### All built-in array functions I could find are supported
    ![built-ins](https://user-images.githubusercontent.com/5202330/48871378-2e9eef00-eded-11e8-8bbc-26c9d675cbeb.png)
    
    ![static key built-ins](https://user-images.githubusercontent.com/5202330/48871517-bd137080-eded-11e8-9208-3725d81b960a.png)
    
    

<br/>
<br/>
<br/>
<hr/>
<hr/>

## Building

The plugin is built with Gradle (IntelliJ Platform Gradle Plugin 2.x) targeting PhpStorm 2026.2.

Requirements: JDK 17+ to run Gradle. A JDK 21 toolchain is needed for compilation — it is downloaded automatically via the foojay toolchain resolver, or install Temurin/JBR 21 yourself.

- Build the plugin zip: `./gradlew buildPlugin` → `build/distributions/deep-assoc-completion-<version>.zip`
- Run a sandboxed PhpStorm 2026.2 with the plugin loaded: `./gradlew runIde`
- Verify the plugin against the target IDE: `./gradlew verifyPlugin`

To use the compiled `.zip` in your PhpStorm go to `Settings -> Plugins -> ⚙ -> Install Plugin from Disk` and select the zip built earlier.

_________________________________________________________

Shutout my thanks to the [JetBrains](https://jb.gg/OpenSource) for continuously supplying me an open source IDE license for this plugin development.
