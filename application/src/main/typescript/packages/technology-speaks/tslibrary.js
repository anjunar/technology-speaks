const { writeFileSync, readFileSync } = require("fs")
const { dirname, join } = require("path")

const getLib = (name) => {
    const lib = dirname(require.resolve("typescript"))
    return readFileSync(join(lib, name), "utf8")
}

const addLib = (name, map) => {
    map.set("/" + name, getLib(name))
}

// recursively add all the .ts files inside a specific directory
const addDir = (dir, map, rootImportName    ) => {
    const fs = require("fs")
    const path = require("path")
    fs.readdirSync(dir).forEach((file) => {
        const fullPath = path.join(dir, file)
        if (fs.statSync(fullPath).isDirectory()) {
            addDir(fullPath, map)
        } else if (file.endsWith(".ts")) {
            map.set("/" + fullPath, fs.readFileSync(fullPath, "utf8"))
        }
    })

}

const createDefaultMap2015 = () => {
    const fsMap = new Map();  // Initialize fsMap as a Map
    addLib("lib.es2015.d.ts", fsMap)
    addLib("lib.es2015.collection.d.ts", fsMap)
    addLib("lib.es2015.core.d.ts", fsMap)
    addLib("lib.es2015.generator.d.ts", fsMap)
    addLib("lib.es2015.iterable.d.ts", fsMap)
    addLib("lib.es2015.promise.d.ts", fsMap)
    addLib("lib.es2015.proxy.d.ts", fsMap)
    addLib("lib.es2015.reflect.d.ts", fsMap)
    addLib("lib.es2015.symbol.d.ts", fsMap)
    addLib("lib.es2015.symbol.wellknown.d.ts", fsMap)
    addLib("lib.dom.d.ts", fsMap)
    addLib("lib.es5.d.ts", fsMap)
    return fsMap
}

// Path: ./public/lib.d.ts
const fsMap = createDefaultMap2015()
const lib = Object.fromEntries(fsMap);

// Write the object as JSON to ./public/lib.d.ts
writeFileSync("./public/lib.json", JSON.stringify(lib, null, 2));
