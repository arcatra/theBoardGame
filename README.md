## Folder Structure

The workspace contains two folders by default, where:

- `src/`: the dir to maintain sources
- `lib/`: the dir to maintain dependencies
- `bin/`: the dir that stores the compiled .class files


## For Linux users

if you're okay with the default compilation, You don't need to manually compile and run this project.
> Note that the compiled .class files will be stored in `./bin/` DIR.
> see `/src/crun.sh` bash file, it is responsible for comiplation and execution of this project.

Make the crun.sh executable, `chmod +x crun.sh`
Run the executable bash file `./crun.sh`

## Config for VScode users

> If you want to configure the folder structure, open `.vscode/settings.json` and update the related settings there.

### Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


