import webpack from 'webpack';
import path from 'path';
import HtmlPlugin from 'html-webpack-plugin';
import CopyWebpackPlugin from 'copy-webpack-plugin';
import * as fs from "node:fs";
import * as https from "node:https";

export default (env) => {
    return {
        entry: './src/index.tsx',
        output: {
            filename: '[name].[contenthash].js',
            path: path.resolve('./build/'),
            clean: true,
            publicPath: env.publicPath
        },
        module: {
            rules: [
                {
                    test: /\.tsx?$/,
                    use: 'ts-loader',
                    exclude: /node_modules/,
                },
                {
                    test: /\.(tsx|ts|js|jsx)$/,
                    use: ['source-map-loader']
                },
                {
                    test: /\.css$/i,
                    use: ["style-loader", "css-loader"]
                },
                {
                    test: /\.(png|jpe?g|gif|svg|eot|ttf|woff|woff2)$/i,
                    type: "asset",
                },
            ],
        },
        resolve: {
            extensions: ['.tsx', '.ts', '.js']
        },
        devServer: {
            hot: true,
            liveReload : true,
            port: 3000,
            open: true,
            historyApiFallback: true,
            allowedHosts: 'all',
            host: '0.0.0.0',
            proxy: [
                {
                    context: ['/service'],
                    target: 'http://localhost:8080',
                },
            ],
        },
        plugins: [
            new HtmlPlugin({
                template: 'public/index.html',
                filename: 'index.html',
                inject: true,
                base: env.publicPath,
                scriptLoading: 'defer',
                preload: [
                    {
                        rel: 'preload',
                        href: 'material.woff2',
                        as: 'font',
                        type: 'font/woff2'
                    }
                ]
            }),
            new CopyWebpackPlugin({
                patterns: [
                    {from: 'public/assets', to: 'assets'},
                ],
            }),
            new webpack.DefinePlugin({
                "process.env.PUBLIC_URL": JSON.stringify(env.publicPath),
            })
        ]
    }
}
