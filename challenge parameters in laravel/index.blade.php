<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Challenges</title>
    <!-- Include necessary CSS stylesheets -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        /* Custom styles */
        .btn-pink {
            background-color: #e83e8c;
            border-color: #e83e8c;
        }
        .btn-pink:hover {
            background-color: #d5377d;
            border-color: #d5377d;
        }
        .alert-pink {
            background-color: #f8d7da;
            border-color: #f5c6cb;
            color: #721c24;
        }
    </style>
</head>
<body>
    <div class="container mt-4">
        <h1 class="mb-4">Challenges</h1>
        <a href="{{ route('challenges.create') }}" class="btn btn-primary mb-3">Create New Challenge</a>

        @if(session('success'))
            <div class="alert alert-success">{{ session('success') }}</div>
        @endif

        <table class="table">
            <thead class="thead-pink">
                <tr>
                    <th>Challenge ID</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                    <th>No. of Questions</th>
                    <th>Duration</th>
                    <th>No. of Attempts</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                @if($challenges->isEmpty())
                    <tr><td colspan="7">No challenges found.</td></tr>
                @else
                    @foreach($challenges as $challenge)
                        <tr>
                            <td>{{ $challenge->challengeID }}</td>
                            <td>{{ $challenge->startDate }}</td>
                            <td>{{ $challenge->endDate }}</td>
                            <td>{{ $challenge->noOfQuestions }}</td>
                            <td>{{ $challenge->duration }}</td>
                            <td>{{ $challenge->noOfAttempts }}</td>
                            <td>
                                <div class="btn-group" role="group">
                                    <a href="{{ route('challenges.show', $challenge->id) }}" class="btn btn-info btn-sm">Show</a>
                                    <a href="{{ route('challenges.edit', $challenge->id) }}" class="btn btn-warning btn-sm">Edit</a>
                                    <form action="{{ route('challenges.destroy', $challenge->id) }}" method="POST" style="display:inline;">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    @endforeach
                @endif
            </tbody>
        </table>
    </div>
    <!-- Include necessary JavaScript libraries or scripts -->
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
    <!-- Include any additional scripts here -->
</body>
</html>
