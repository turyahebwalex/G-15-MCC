<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Answers</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #fce4ec; /* Light pink background */
            color: #333;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff; /* White background for form */
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        h1 {
            color: #d81b60; /* Pink color */
            margin-bottom: 20px;
        }
        .success-message {
            color: #4caf50; /* Success message color */
            font-size: 1.1em;
            margin-bottom: 20px;
        }
        form {
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        input[type="file"] {
            margin-bottom: 20px;
            border: 2px solid #d81b60;
            border-radius: 4px;
            padding: 10px;
            font-size: 1em;
            width: 100%;
            max-width: 400px;
        }
        button {
            background-color: #d81b60; /* Pink button */
            color: white;
            border: none;
            border-radius: 4px;
            padding: 10px 20px;
            font-size: 1em;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        button:hover {
            background-color: #c2185b; /* Darker pink on hover */
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Upload Answers</h1>

        @if (session('success'))
            <div class="success-message">{{ session('success') }}</div>
        @endif

        <form action="{{ route('answers.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <input type="file" name="file" />
            <button type="submit">Upload Answers</button>
        </form>
    </div>
</body>
</html>

