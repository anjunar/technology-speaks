const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin')    ;
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = {
    mode: 'development',
    entry: './src/client.tsx',
    devtool: 'inline-source-map',
    output: {
        path: path.resolve(__dirname, './dist/client'),
        filename: 'main.js',
        publicPath: '/static/',
    },
    resolve: {
        extensions: ['.js', '.ts', '.tsx'],
    },
    module: {
        rules: [
            {
                test: /\.(ts|tsx)$/,
                use: 'ts-loader',
                exclude: /node_modules/,
            },
            {
                test: /\.(tsx|ts|js|jsx)$/,
                use: ['source-map-loader']
            },
            {
                test: /\.css$/i,
                use: [
                    {
                        loader: MiniCssExtractPlugin.loader,
                        options: {
                            emit: true,
                        },
                    },
                    "css-loader"
                ],
            },
            {
                test: /\.(png|jpe?g|gif|svg|eot|ttf|woff|woff2)$/i,
                type: "asset",
            }
        ],
    },
    devServer: {
        // hot: true,
        // liveReload : true,
        port: 3001,
        historyApiFallback: true,
        compress: false,
        proxy: [
            {
                context: ['/service'],
                target: 'http://localhost:8080',
                ws: true,
                changeOrigin: true
            }
        ],
    },
    target: 'web',
    plugins: [
        new CopyWebpackPlugin({
            patterns: [
                {from: 'public/assets', to: 'assets'},
            ],
        }),
        new MiniCssExtractPlugin({
            filename: 'assets/style.css',
        })
    ]
};