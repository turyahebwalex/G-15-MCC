<?php


namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\AnswersImport;

class AnswerImportController extends Controller
{
    public function importForm()
    {
        return view('import'); // Return the view with the form for file upload
    }

    public function import(Request $request)
    {
        // Validate the uploaded file
        $request->validate([
            'file' => 'required|file|mimes:xlsx,csv'
        ]);

        // Import the data using the AnswersImport class
        Excel::import(new AnswersImport, $request->file('file'));

        // Redirect or return a response
        return redirect()->back()->with('success', 'Answers imported successfully!');
    }
}
