const webpack = require('webpack');
const path = require('path');
const nodeExternals = require('webpack-node-externals');
const CopyWebpackPlugin = require("copy-webpack-plugin");
const {CleanWebpackPlugin} = require("clean-webpack-plugin");

module.exports = {
    mode: 'development',
    entry: './server.tsx',
    devtool: 'inline-source-map',
    output: {
        path: path.resolve(__dirname, './dist/server'),
        filename: 'server.js',
    },
    target: 'node',
    externals: [nodeExternals()],
    resolve: {
        extensions: ['.js', '.ts', '.tsx'],
    },
    module: {
        rules: [
            {
                test: /\.(ts|tsx)$/,
                use: {
                    loader: 'ts-loader',
                },
                exclude: /node_modules|\.d\.ts$/,
            },
            {
                test: /\.css$/i,
                use: ["style-loader", "css-loader"]
            },
            {
                test: /\.d\.ts$/i,
                type: "asset/source"
            },
            {
                test: /\.(png|jpe?g|gif|svg|eot|ttf|woff|woff2)$/i,
                type: "asset",
            }
        ],
    },
    plugins: [
        new CopyWebpackPlugin({
            patterns: [
                {from: 'public', to: 'public'},
            ],
        })
    ]
};
