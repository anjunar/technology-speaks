const webpack = require('webpack');
const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin')    ;
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const nodeExternals = require('webpack-node-externals');
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = [
    {
        name : "client",
        mode: 'development',
        entry: [
            './src/client.tsx'
        ],
        devtool: 'inline-source-map',
        output: {
            filename: '[name].[contenthash].js',
            chunkFilename: '[name].[contenthash].js',
            path: path.resolve(__dirname, 'dist', "client"),
            clean: true,
            publicPath : '/static/'
        },
        optimization: {
            splitChunks: {
                chunks: 'all',
                cacheGroups: {
                    vendor: {
                        test: /[\\/]node_modules[\\/]/,
                        name: 'vendor',
                        chunks: 'all',
                        enforce: true,
                    },
                },
            },
        },
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
                    test: /.(tsx|ts|js|jsx)$/,
                    use: ['source-map-loader'],
                    exclude: [/node_modules[\\/]typescript/, /\.d\.ts/]
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
                    test: /\.d\.ts$/i,
                    type: "asset/source"
                },
                {
                    test: /\.(png|jpe?g|gif|svg|eot|ttf|woff|woff2)$/i,
                    type: "asset/resource"
                }
            ],
        },
        devServer: {
            hot: true,
            liveReload : true,
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
            new HtmlWebpackPlugin({
                template: './public/index.html',
                filename: 'index.html',
                inject: 'body',
            }),
            new CopyWebpackPlugin({
                patterns: [
                    {from: 'public/assets', to: 'assets'},
                ],
            }),
            new MiniCssExtractPlugin({
                filename: 'assets/style.css',
            })
        ],
        ignoreWarnings: [
            {
                module: /node_modules[\\/]@typescript[\\/]vfs/,
                message: /Critical dependency: the request of a dependency is an expression/,
            },
            {
                module: /node_modules[\\/]typescript/,
                message: /Critical dependency: the request of a dependency is an expression/,
            },
        ]
    },
    {
        name : "server",
        mode: 'development',
        entry: './server.tsx',
        devtool: 'inline-source-map',
        output: {
            path: path.resolve(__dirname, './dist/server'),
            filename: 'server.js',
        },
        target: 'node',
        externals: [nodeExternals({
            modulesDir: path.resolve(__dirname, '../../node_modules'),
            allowlist: [/^react-ui-simplicity/]
        })],
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
            }),
            new MiniCssExtractPlugin({
                filename: 'assets/style.css',
            })
        ]
    }
]
